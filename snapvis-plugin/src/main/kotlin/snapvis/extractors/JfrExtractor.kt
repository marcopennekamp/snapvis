package snapvis.extractors

import FlameGraph
import one.jfr.ClassRef
import one.jfr.JfrReader
import one.jfr.StackTrace
import one.jfr.event.Event
import one.jfr.event.ExecutionSample
import snapvis.metrics.CallMetrics
import snapvis.metrics.MethodCallTime
import snapvis.util.Nanoseconds
import snapvis.util.commonPrefixSize
import snapvis.util.normalizeClassName
import java.nio.charset.Charset
import java.nio.file.Path

object JfrExtractor : Extractor {
    /**
     * Extracts method execution timings from the JFR file [filePath].
     *
     * The JFR format contains timing and allocation events and their associated information. In general, variable-size
     * data such as strings, methods, classes, etc. are held in metadata and constant tables. They are referred to via
     * IDs, for example [Event.stackTraceId] which refers to a [StackTrace] in [JfrReader.stackTraces].
     *
     * For method execution time, of particular interest are the [ExecutionSample]s, which are events that record the
     * stack trace at some specific sampling point. Two adjacent samples can be analysed for their common methods to
     * gauge, roughly, which method calls haven't finished yet and have thus been executing for the duration delta.
     *
     * This approach comes with two major issues:
     *
     *  1. Calls to small methods might be missed altogether. In such cases, the extractor won't have any data for the
     *     method call.
     *  2. If a method call appears in two adjacent stack traces, the extractor must assume that the second call is
     *     still the same call as the first one. However, in some cases, between the two sampling points, the method
     *     might have finished and been called again from the same location. The extractor doesn't have enough
     *     information to separate the method calls, so an error will be introduced into the per-call execution time.
     *     This is especially egregious in tight benchmark loops, where the same method is called millions of times and
     *     virtually every sample will contain the method call.
     *
     * Both of these issues are lessened with a correctly chosen sampling interval. This choice strongly depends on the
     * program being profiled. Profiling some especially short programs or tight benchmark loops might not produce
     * useful call time data using a sampling approach at all.
     */
    override fun extract(filePath: Path): CallMetrics = JfrReader(filePath.toString()).use { reader ->
        JfrExtractorImpl(reader).extract()
    }
}

/**
 * [MethodCall] represents a method call of [calleeId] that occurred from [callerId] at a specific [line] (where the
 * caller is defined).
 */
private data class MethodCall(val calleeId: Long, val callerId: Long, val callerType: Byte, val line: Int)

/**
 * A [MethodCallStack] is a reversed (first call on top) [StackTrace] with the method call's caller and callee placed
 * in the same entry. This is in contrast to [StackTrace], where the callee only becomes apparent in the entry
 * *following* the caller entry.
 */
private data class MethodCallStack(val entries: List<MethodCall>) : List<MethodCall> by entries

/**
 * [MethodCallTotals] aggregates stats about a particular [methodCall].
 */
private class MethodCallTotals(val methodCall: MethodCall) {
    var callCount: Int = 0
    var totalTime: Nanoseconds = Nanoseconds(0)
}

private class JfrExtractorImpl(val reader: JfrReader) {

    fun extract(): CallMetrics = convertToMetrics(getTotalsByCall())

    /**
     * [getTotalsByCall] collects the approximate total time and number of times a method was called from all
     * [ExecutionSample]s available in the JFR file.
     */
    private fun getTotalsByCall(): Map<MethodCall, MethodCallTotals> {
        val totalsByCall = HashMap<MethodCall, MethodCallTotals>()

        // This map keeps track of the most recently recorded execution sample and method call stack per thread ID. The
        // snapshot may contain data for multiple threads which each have their own stack traces.
        data class SampleInfo(val sample: ExecutionSample, val methodCallStack: MethodCallStack)
        val previousByThread = HashMap<Int, SampleInfo>()

        val samples = reader.readAllEvents(ExecutionSample::class.java)
        for (sample in samples) {
            val previous = previousByThread[sample.tid]
            val methodCallStack = reformat(reader.stackTraces.get(sample.stackTraceId.toLong()))

            if (previous != null) {
                // The sample time (in ticks) stands in relation to the previous sample.
                val duration = ticksToNanoseconds(sample.time - previous.sample.time)

                // Depending on whether a method is part of the current and/or previous trace, we update the method
                // call totals as such:
                //  - In current trace, but not previous: Method call begins. (Increment call counter.)
                //  - In common trace: Method call continues. (Add elapsed time.)
                //  - In previous trace, but not current: Method call ends. (Nothing to do.)
                val commonPrefixSize = previous.methodCallStack.commonPrefixSize(methodCallStack)
                for (index in 0 until methodCallStack.size) {
                    val methodCall = methodCallStack[index]

                    // Native method callers should be ignored as the plugin isn't able to annotate native code.
                    if (!isNativeMethod(methodCall.callerId, methodCall.callerType)) {
                        val totals = totalsByCall.getOrPut(methodCall) { MethodCallTotals(methodCall) }
                        if (index < commonPrefixSize) {
                            totals.totalTime += duration // Method call continues
                        } else {
                            totals.callCount += 1 // Method call begins
                        }
                    }
                }
            }

            previousByThread[sample.tid] = SampleInfo(sample, methodCallStack)
        }

        return totalsByCall
    }

    private fun reformat(stackTrace: StackTrace): MethodCallStack {
        val entries = ArrayList<MethodCall>(stackTrace.methods.size)

        // The stack trace's top element is last in the list, so we are iterating backwards. Because a method ID in a
        // stack trace points to the *caller*, we need the next element of the stack trace to get the *callee* method
        // ID. For the bottom element of the stack trace, this callee ID doesn't exist, so we are excluding it from the
        // result stack trace.
        for (index in stackTrace.methods.size - 1 downTo 1) {
            entries.add(
                MethodCall(
                    stackTrace.methods[index - 1],
                    stackTrace.methods[index],
                    stackTrace.types[index],
                    // A location is an aggregate of two 4-byte integers: Line number and bytecode index.
                    stackTrace.locations[index] ushr 16,
                )
            )
        }

        return MethodCallStack(entries)
    }

    private fun convertToMetrics(totalsByCall: Map<MethodCall, MethodCallTotals>): CallMetrics {
        val metrics = CallMetrics()

        for (totals in totalsByCall.values) {
            val callInfo = totals.methodCall
            val callerClassName = normalizeClassName(getMethodClassName(callInfo.callerId))
            val classMetrics = metrics.get(callerClassName)

            if (totals.callCount > 0 && totals.totalTime.ns > 0) {
                val methodName = getMethodName(callInfo.calleeId)
                val timePerCall = totals.totalTime / totals.callCount
                classMetrics.add(callInfo.line, MethodCallTime(methodName, timePerCall))
            }
        }

        return metrics
    }

    private fun isNativeMethod(methodId: Long, methodType: Byte): Boolean {
        val className = getMethodClassName(methodId)
        return methodType >= FlameGraph.FRAME_NATIVE && methodType <= FlameGraph.FRAME_KERNEL || className.isEmpty()
    }

    private fun getMethodName(methodId: Long): String = nameAsString(reader.symbols[reader.methods[methodId].name])
    private fun getMethodClass(methodId: Long): ClassRef = reader.classes[reader.methods[methodId].cls]
    private fun getMethodClassName(methodId: Long): String = nameAsString(reader.symbols[getMethodClass(methodId).name])

    /**
     * Turns a [ByteArray] name into a string. Per the JVM spec, names are
     * [encoded in UTF-8](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.2).
     */
    private fun nameAsString(array: ByteArray): String = array.toString(Charset.forName("UTF-8"))

    private fun ticksToNanoseconds(ticks: Long): Nanoseconds = Nanoseconds(ticks * 1000000000 / reader.ticksPerSec)

}

package snapvis.metrics

import snapvis.util.Nanoseconds

/**
 * Contains all [MethodCallTime]s per class via [ClassCallMetrics].
 */
class CallMetrics {
    private val byClass: MutableMap<String, ClassCallMetrics> = HashMap()

    /**
     * Returns the existing [ClassCallMetrics] for [className] or creates a new [ClassCallMetrics] instance.
     */
    fun get(className: String): ClassCallMetrics = byClass.getOrPut(className) { ClassCallMetrics() }
}

/**
 * Contains the [MethodCallTime]s that occur in a given Kotlin class on each line. A line may contain multiple metrics.
 */
class ClassCallMetrics {
    private val byLine: MutableMap<Int, List<MethodCallTime>> = HashMap()

    private fun get(line: Int): List<MethodCallTime> = byLine.getOrDefault(line, emptyList())

    /**
     * Returns the [MethodCallTime] with [methodName] on [line], if it exists. If a line has multiple [MethodCallTime]s
     * with the same method name, the first [MethodCallTime] is preferred.
     */
    fun get(line: Int, methodName: String): MethodCallTime? = get(line).find { it.methodName == methodName }

    /**
     * Adds [metric] to the metrics on [line].
     */
    fun add(line: Int, metric: MethodCallTime) {
        byLine.merge(line, listOf(metric), Collection<MethodCallTime>::plus)
    }
}

data class MethodCallTime(val methodName: String, val timePerCall: Nanoseconds)

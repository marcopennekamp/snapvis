package snapvis

import com.intellij.openapi.project.Project
import snapvis.extractors.Extractors
import snapvis.metrics.CallMetrics
import snapvis.metrics.MethodCallTime
import snapvis.metrics.MetricsService
import snapvis.util.Nanoseconds
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * [SampleCalculatorSnapshot] helps several tests extract and verify the sample calculator snapshot.
 */
object SampleCalculatorSnapshot {
    fun extract(): CallMetrics {
        val fileName = javaClass.classLoader.getResource("calculator_snapshot.jfr")?.file
        assertNotNull(fileName)
        val extractor = Extractors.forExtension("jfr")
        assertNotNull(extractor)
        return extractor.extract(fileName)
    }

    fun verifyMetrics(metrics: CallMetrics) {
        // Note that these nanosecond timings aren't necessarily representative of the calculator's actual run time
        // because the sampling rate of the snapshot is quite low. What the test should ensure is that these method
        // call instances are discovered in the snapshot and the execution times are consistently calculated.
        val callTime1 = metrics.get("calculator.Tokenizer").get(61, "toDoubleOrNull")
        assertNotNull(callTime1)
        assertEquals(MethodCallTime("toDoubleOrNull", Nanoseconds(207065)), callTime1)

        val callTime2 = metrics.get("calculator.EvaluatorKt").get(27, "removeLast")
        assertNotNull(callTime2)
        assertEquals(MethodCallTime("removeLast", Nanoseconds(103637)), callTime2)

        val callTime3 = metrics.get("calculator.CalculatorKt").get(13, "calculate")
        assertNotNull(callTime3)
        assertEquals(MethodCallTime("calculate", Nanoseconds(17486188)), callTime3)
    }

    /**
     * Sets up the calculator snapshot in [MetricsService].
     */
    fun setUp(project: Project) {
        val metricsService = project.getService(MetricsService::class.java)
        assertNotNull(metricsService)
        val metrics = extract()
        metricsService.callMetrics = metrics
    }

    /**
     * Removes the calculator snapshot metrics from [MetricsService].
     */
    fun tearDown(project: Project) {
        val metricsService = project.getService(MetricsService::class.java)
        assertNotNull(metricsService)
        metricsService.callMetrics = null
    }
}

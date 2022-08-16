package snapvis

import com.intellij.openapi.project.Project
import snapvis.actions.getResourcePath
import snapvis.extractors.Extractors
import snapvis.metrics.CallMetrics
import snapvis.metrics.MethodCallTime
import snapvis.metrics.MetricsService
import snapvis.metrics.getMetricsService
import snapvis.util.ns
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * [SampleCalculatorSnapshot] helps several tests extract and verify the sample calculator snapshot.
 */
object SampleCalculatorSnapshot {
    val SNAPSHOT_FILE_PATH: Path = getResourcePath("calculator_snapshot.jfr")

    fun extract(): CallMetrics {
        val extractor = Extractors.forExtension(SNAPSHOT_FILE_PATH.extension)
        assertNotNull(extractor)
        return extractor.extract(SNAPSHOT_FILE_PATH)
    }

    fun verifyMetrics(metrics: CallMetrics) {
        // Note that these nanosecond timings aren't necessarily representative of the calculator's actual run time
        // because the sampling rate of the snapshot is quite low. What the test should ensure is that these method
        // call instances are discovered in the snapshot and the execution times are consistently calculated.
        val callTime1 = metrics.get("calculator.Tokenizer").get(61, "toDoubleOrNull")
        assertNotNull(callTime1)
        assertEquals(MethodCallTime("toDoubleOrNull", 207065.ns), callTime1)

        val callTime2 = metrics.get("calculator.EvaluatorKt").get(27, "removeLast")
        assertNotNull(callTime2)
        assertEquals(MethodCallTime("removeLast", 103637.ns), callTime2)

        val callTime3 = metrics.get("calculator.CalculatorKt").get(13, "calculate")
        assertNotNull(callTime3)
        assertEquals(MethodCallTime("calculate", 17486188.ns), callTime3)
    }

    /**
     * Loads the calculator snapshot into [MetricsService].
     */
    fun load(project: Project) {
        val metricsService = project.getMetricsService()
        assertNotNull(metricsService)
        val metrics = extract()
        metricsService.callMetrics = metrics
    }

    /**
     * Removes the calculator snapshot metrics from [MetricsService].
     */
    fun clear(project: Project) {
        val metricsService = project.getMetricsService()
        assertNotNull(metricsService)
        metricsService.callMetrics = null
    }
}

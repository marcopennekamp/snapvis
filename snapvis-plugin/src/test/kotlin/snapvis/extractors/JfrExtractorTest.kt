package snapvis.extractors

import snapvis.SampleCalculatorSnapshot
import kotlin.test.Test

class JfrExtractorTest {
    @Test
    fun `JfrExtractor extracts the correct call metrics from the sample calculator JFR snapshot`() {
        val metrics = SampleCalculatorSnapshot.extract()
        SampleCalculatorSnapshot.verifyMetrics(metrics)
    }
}

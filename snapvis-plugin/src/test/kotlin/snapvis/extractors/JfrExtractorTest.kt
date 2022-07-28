package snapvis.extractors

import snapvis.metrics.MethodCallTime
import snapvis.util.Nanoseconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JfrExtractorTest {
    @Test
    fun `JfrExtractor extracts the correct call metrics from a sample JFR file`() {
        val fileName = javaClass.classLoader.getResource("sample_snapshot.jfr")?.file
        assertNotNull(fileName)
        val extractor = Extractors.forExtension("jfr")
        assertNotNull(extractor)
        val metrics = extractor.extract(fileName)

        // Note that these nanosecond timings aren't necessarily representative of the calculator's actual run time
        // because the sampling rate of the snapshot is quite low. What the test should ensure is that these method
        // call instances are discovered in the snapshot and the execution times are consistently calculated.
        val callTime1 = metrics.get("calculator.Tokenizer").get(61, "toDoubleOrNull")
        assertNotNull(callTime1)
        assertEquals(MethodCallTime("toDoubleOrNull", Nanoseconds(2581300)), callTime1)

        val callTime2 = metrics.get("calculator.EvaluatorKt").get(37, "addLast")
        assertNotNull(callTime2)
        assertEquals(MethodCallTime("addLast", Nanoseconds(253140)), callTime2)

        val callTime3 = metrics.get("calculator.CalculatorKt").get(13, "calculate")
        assertNotNull(callTime3)
        assertEquals(MethodCallTime("calculate", Nanoseconds(138848335)), callTime3)
    }
}

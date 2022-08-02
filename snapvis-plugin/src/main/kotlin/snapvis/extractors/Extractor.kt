package snapvis.extractors

import snapvis.metrics.CallMetrics
import java.nio.file.Path

interface Extractor {
    /**
     * Extracts profiling data from a file [filePath] and summarizes method execution time in the returned
     * [CallMetrics]. Method calls with a measured call time of 0ns should be omitted from the results.
     */
    fun extract(filePath: Path): CallMetrics
}

object Extractors {
    private val extractorByExtension = mapOf(
        "jfr" to JfrExtractor,
    )

    /**
     * Returns an [Extractor] that can process a file with [extension].
     *
     * Supported extensions: jfr.
     */
    fun forExtension(extension: String): Extractor? = extractorByExtension[extension]

    /**
     * Whether there is an extractor for lower-case [extension].
     */
    fun supportsExtension(extension: String): Boolean = extractorByExtension.containsKey(extension)
}

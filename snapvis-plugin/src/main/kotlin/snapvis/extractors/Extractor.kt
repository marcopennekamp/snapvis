package snapvis.extractors

import snapvis.metrics.CallMetrics

interface Extractor {
    /**
     * Extracts profiling data from a file [fileName] and summarizes method execution time in the returned
     * [CallMetrics].
     */
    fun extract(fileName: String): CallMetrics
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

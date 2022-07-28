package snapvis.hints

import com.intellij.codeInsight.hints.InlayHintsPassFactory

@Suppress("UnstableApiUsage")
object CallTimeHints {
    /**
     * Forces all inlay hints in all editors to reload.
     *
     * This is a sledgehammer approach to reloading call time hints. An alternative would be to write our own
     * highlighting pass, akin to [com.intellij.codeInsight.hints.ParameterHintsPass]. This is an option for future
     * improvements.
     */
    fun reload() {
        InlayHintsPassFactory.forceHintsUpdateOnNextPass()
    }
}

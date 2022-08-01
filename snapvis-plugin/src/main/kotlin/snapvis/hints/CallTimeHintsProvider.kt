package snapvis.hints

import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.ui.dsl.builder.panel
import snapvis.SnapvisBundle
import javax.swing.JComponent

@Suppress("UnstableApiUsage")
class CallTimeHintsProvider : InlayHintsProvider<NoSettings> {

    // TODO: Bug? When using the settings key `snapvis.hints.call_time`, the IDE at startup tries to get a description
    //       during initialization from the KotlinBundle (!), NOT the SnapvisBundle. The workaround of using a key
    //       that's contained in the KotlinBundle is untenable for production usage of the plugin. Either I am missing
    //       something about resource bundles, or this is a bug if nobody has actually written a third-party plugin
    //       that provides inlay hints for a language.
    override val key: SettingsKey<NoSettings> = SettingsKey("snapvis.hints.call_time")
    //override val key: SettingsKey<NoSettings> = SettingsKey("fix.make.data.class")
    override val name: String = SnapvisBundle.getMessage("snapvis.hints.call_time")
    override val previewText: String? = null // TODO: Provide a preview in the settings.

    override fun createSettings(): NoSettings = NoSettings()

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector? = editor.project?.let { CallTimeHintsCollector(it, editor) }

    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable = object : ImmediateConfigurable {
        override fun createComponent(listener: ChangeListener): JComponent = panel { }
    }

}

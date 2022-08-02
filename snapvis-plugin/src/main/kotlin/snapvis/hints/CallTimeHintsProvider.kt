package snapvis.hints

import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.ui.dsl.builder.panel
import snapvis.SnapvisBundle
import javax.swing.JComponent

@Suppress("UnstableApiUsage")
class CallTimeHintsProvider : InlayHintsProvider<NoSettings> {
    override val key: SettingsKey<NoSettings> = SettingsKey("snapvis.hints.call_time.name")
    override val name: String = SnapvisBundle.getMessage("snapvis.hints.call_time.name")
    override val description: String
        get() = SnapvisBundle.getMessage("snapvis.hints.call_time.description")
    override val previewText: String? = null

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

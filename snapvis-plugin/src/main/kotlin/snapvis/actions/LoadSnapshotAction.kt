package snapvis.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import snapvis.SnapvisBundle
import snapvis.extractors.Extractors
import snapvis.metrics.getMetricsService
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.extension

class LoadSnapshotAction : AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        chooseSnapshot(project) { extractSnapshot(project, it.toNioPath()) }
    }

    private fun chooseSnapshot(project: Project, callback: (VirtualFile) -> Unit) {
        FileChooser.chooseFile(FILE_CHOOSER_DESCRIPTOR, project, null, callback)
    }

    fun extractSnapshot(project: Project, filePath: Path) {
        if (!filePath.exists()) {
            Messages.showMessageDialog(
                project,
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.file_not_found.message", filePath),
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.file_not_found.title"),
                Messages.getErrorIcon(),
            )
            return
        }

        val extractor = Extractors.forExtension(filePath.extension)
        if (extractor == null) {
            Messages.showMessageDialog(
                project,
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.unknown_format.message", filePath.extension),
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.unknown_format.title"),
                Messages.getErrorIcon(),
            )
            return
        }

        project.getMetricsService().callMetrics = extractor.extract(filePath)
        Messages.showMessageDialog(
            project,
            SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.success.message"),
            SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.success.title"),
            Messages.getInformationIcon(),
        )
    }

    companion object {
        val FILE_CHOOSER_DESCRIPTOR: FileChooserDescriptor =
            FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor().withFileFilter { file ->
                val extension = file.extension
                extension != null && Extractors.supportsExtension(extension.lowercase())
            }
    }
}

package snapvis.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import snapvis.SnapvisBundle
import snapvis.extractors.Extractors
import snapvis.metrics.MetricsService

// TODO: Write a simple test.

class LoadSnapshotAction : AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { project ->
            chooseSnapshot(project) { extractSnapshot(project, it) }
        }
    }

    private fun chooseSnapshot(project: Project, callback: (VirtualFile) -> Unit) {
        val descriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor().withFileFilter { file ->
            val extension = file.extension
            extension != null && Extractors.supportsExtension(extension.lowercase())
        }
        FileChooser.chooseFile(descriptor, project, null, callback)
    }

    private fun extractSnapshot(project: Project, file: VirtualFile) {
        val extractor = Extractors.forExtension(file.extension ?: "")
        if (extractor != null) {
            val metricsService = project.getService(MetricsService::class.java)
            metricsService.callMetrics = extractor.extract(file.path)
            Messages.showMessageDialog(
                project,
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.success.message"),
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.success.title"),
                Messages.getInformationIcon(),
            )
        } else {
            Messages.showMessageDialog(
                project,
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.unknown_format.message", file.extension),
                SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.unknown_format.title"),
                Messages.getErrorIcon(),
            )
        }
    }
}

package snapvis

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import snapvis.extractors.Extractors
import snapvis.metrics.MetricsService

// TODO: Rename.
// TODO: Move to `actions` package.
// TODO: Write a simple test.

class TestAction : AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        if (project != null) {
            val metricsService = project.getService(MetricsService::class.java)
            val fileExtension = "jfr"
            val extractor = Extractors.forExtension(fileExtension)
            if (extractor != null) {
                metricsService.callMetrics = extractor.extract("/home/marco/snapshot.jfr")
                // TODO: Invalidate / reload all inlay hints...
                Messages.showMessageDialog(
                    project,
                    "The snapshot has been loaded successfully. Hints should now become available.", // TODO: i18n.
                    "Snapshot Loaded", // TODO: i18n.
                    Messages.getInformationIcon(),
                )
            } else {
                Messages.showMessageDialog(
                    project,
                    "Cannot load a CPU snapshot with a ??? file extension.", // TODO: i18n.
                    "Unknown Snapshot Format", // TODO: i18n.
                    Messages.getErrorIcon(),
                )
            }
        }
    }
}

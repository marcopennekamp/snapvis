package snapvis.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import snapvis.metrics.getMetricsService

class ClearSnapshotAction : AnAction() {
    override fun update(event: AnActionEvent) {
        val project = event.project
        if (project != null) {
            event.presentation.isVisible = true
            event.presentation.isEnabled = project.getMetricsService().callMetrics != null
        } else {
            event.presentation.isEnabledAndVisible = false
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { project ->
            project.getMetricsService().callMetrics = null
        }
    }
}

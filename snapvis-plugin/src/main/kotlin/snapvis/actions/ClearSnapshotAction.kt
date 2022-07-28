package snapvis.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import snapvis.metrics.MetricsService

// TODO: Write a simple test.

class ClearSnapshotAction : AnAction() {
    override fun update(event: AnActionEvent) {
        val project = event.project
        if (project != null) {
            event.presentation.isVisible = true
            event.presentation.isEnabled = project.getService(MetricsService::class.java).callMetrics != null
        } else {
            event.presentation.isEnabledAndVisible = false
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let { project ->
            val metricsService = project.getService(MetricsService::class.java)
            metricsService.callMetrics = null
        }
    }
}

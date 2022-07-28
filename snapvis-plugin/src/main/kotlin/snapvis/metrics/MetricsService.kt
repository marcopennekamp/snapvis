package snapvis.metrics

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

/**
 * The [MetricsService] keeps project-level records about metrics (currently only [CallMetrics]) which the plugin's
 * hints need access to.
 */
@Service
class MetricsService(val project: Project) {
    /**
     * The [CallMetrics] of the service are always defined, but only populated once the user opens a snapshot. Until
     * then, the metrics will simply be empty.
     */
    var callMetrics: CallMetrics = CallMetrics()
}

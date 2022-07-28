package snapvis.metrics

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import snapvis.hints.CallTimeHints

/**
 * The [MetricsService] keeps project-level records about metrics (currently only [CallMetrics]) which the plugin's
 * hints need access to.
 */
@Service
class MetricsService(val project: Project) {
    /**
     * The [CallMetrics] of the service are always defined, but only populated once the user opens a snapshot. Until
     * then, the metrics will simply be empty.
     *
     * Immediately reloads call time hints when this property is set to a new value.
     */
    var callMetrics: CallMetrics = CallMetrics()
        set(value) {
            field = value
            CallTimeHints.reload()
        }
}

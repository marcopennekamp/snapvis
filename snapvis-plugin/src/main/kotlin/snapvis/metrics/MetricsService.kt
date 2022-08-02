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
     * [callMetrics] are only defined once the user has loaded a snapshot. Clearing a snapshot removes these call
     * metrics again.
     *
     * The setter immediately reloads all call time hints.
     */
    var callMetrics: CallMetrics? = null
        set(value) {
            field = value
            CallTimeHints.reload()
        }
}

fun Project.getMetricsService(): MetricsService = this.getService(MetricsService::class.java)

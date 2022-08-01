package snapvis

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import snapvis.metrics.MetricsService
import snapvis.metrics.getMetricsService

abstract class SnapvisPlatformTestCase : BasePlatformTestCase() {
    protected val metricsService: MetricsService
        get() = project.getMetricsService()
}

package snapvis.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import snapvis.SampleCalculatorSnapshot
import snapvis.metrics.getMetricsService
import kotlin.test.Test

class ClearSnapshotActionTest : BasePlatformTestCase() {
    @Test
    fun testUpdateHidden() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(null)

        // `update` should hide the action if no project is defined.
        action.update(event)
        assert(!event.presentation.isVisible)
        assert(!event.presentation.isEnabled)
    }

    @Test
    fun testUpdateVisibleDisabled() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(myFixture.project)

        // `update` should show but disables the action if a project but no call metrics are defined.
        action.update(event)
        assert(event.presentation.isVisible)
        assert(!event.presentation.isEnabled)
    }

    @Test
    fun testUpdateVisibleEnabled() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(myFixture.project)
        SampleCalculatorSnapshot.load(myFixture.project)

        // `update` should show and enable the action if a project and call metrics are defined.
        action.update(event)
        assert(event.presentation.isVisible)
        assert(event.presentation.isEnabled)
    }

    @Test
    fun testActionPerformed() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(myFixture.project)
        SampleCalculatorSnapshot.load(myFixture.project)

        // `actionPerformed` should clear the call metrics.
        action.actionPerformed(event)
        assertNull(myFixture.project.getMetricsService().callMetrics)
    }

    @Test
    fun testActionPerformedAlreadyCleared() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(myFixture.project)

        // `actionPerformed` should work even if the call metrics are already cleared.
        val metricsService = myFixture.project.getMetricsService()
        assertNull(metricsService.callMetrics)
        action.actionPerformed(event)
        assertNull(metricsService.callMetrics)
    }

    @Test
    fun testActionPerformedNoProject() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(null)
        SampleCalculatorSnapshot.load(myFixture.project)

        // `actionPerformed` should do nothing if there is no project.
        action.actionPerformed(event)
        assertNotNull(myFixture.project.getMetricsService().callMetrics)
    }

    override fun tearDown() {
        SampleCalculatorSnapshot.clear(myFixture.project)
        super.tearDown()
    }
}

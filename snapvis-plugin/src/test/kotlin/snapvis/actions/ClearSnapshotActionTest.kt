package snapvis.actions

import snapvis.SampleCalculatorSnapshot
import snapvis.SnapvisPlatformTestCase
import kotlin.test.Test

class ClearSnapshotActionTest : SnapvisPlatformTestCase() {
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
        val event = freshActionEvent(project)

        // `update` should show but disable the action if a project but no call metrics are defined.
        action.update(event)
        assert(event.presentation.isVisible)
        assert(!event.presentation.isEnabled)
    }

    @Test
    fun testUpdateVisibleEnabled() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(project)
        SampleCalculatorSnapshot.load(project)

        // `update` should show and enable the action if a project and call metrics are defined.
        action.update(event)
        assert(event.presentation.isVisible)
        assert(event.presentation.isEnabled)
    }

    @Test
    fun testActionPerformed() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(project)
        SampleCalculatorSnapshot.load(project)

        // `actionPerformed` should clear the call metrics.
        action.actionPerformed(event)
        assertNull(metricsService.callMetrics)
    }

    @Test
    fun testActionPerformedAlreadyCleared() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(project)

        // `actionPerformed` should work even if the call metrics are already cleared.
        assertNull(metricsService.callMetrics)
        action.actionPerformed(event)
        assertNull(metricsService.callMetrics)
    }

    @Test
    fun testActionPerformedNoProject() {
        val action = ClearSnapshotAction()
        val event = freshActionEvent(null)
        SampleCalculatorSnapshot.load(project)

        // `actionPerformed` should do nothing if there is no project.
        action.actionPerformed(event)
        assertNotNull(metricsService.callMetrics)
    }

    override fun tearDown() {
        SampleCalculatorSnapshot.clear(project)
        super.tearDown()
    }
}

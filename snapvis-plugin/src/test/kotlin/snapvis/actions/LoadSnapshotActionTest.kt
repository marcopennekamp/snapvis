package snapvis.actions

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileSystem
import snapvis.SampleCalculatorSnapshot
import snapvis.SnapvisBundle
import snapvis.SnapvisPlatformTestCase
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.test.Test

class LoadSnapshotActionTest : SnapvisPlatformTestCase() {
    @Test
    fun testUpdateHidden() {
        val action = LoadSnapshotAction()
        val event = freshActionEvent(null)

        // `update` should hide the action if no project is defined.
        action.update(event)
        assert(!event.presentation.isVisible)
        assert(!event.presentation.isEnabled)
    }

    @Test
    fun testUpdateVisibleEnabled() {
        val action = LoadSnapshotAction()
        val event = freshActionEvent(project)

        // `update` should show and enable the action if a project is defined.
        action.update(event)
        assert(event.presentation.isVisible)
        assert(event.presentation.isEnabled)
    }

    @Test
    fun testActionPerformedNoProject() {
        val action = LoadSnapshotAction()
        val event = freshActionEvent(null)

        // `actionPerformed` should do nothing if there is no project.
        action.actionPerformed(event)
        assertNull(metricsService.callMetrics)
    }

    @Test
    fun testExtractSnapshot() {
        // `extractSnapshot` should load a snapshot.
        testExtractSnapshot(
            SampleCalculatorSnapshot.SNAPSHOT_FILE_PATH,
            SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.success.message"),
            true,
        )
    }

    @Test
    fun testActionPerformedFileNotFound() {
        // `extractSnapshot` should show an error dialog if the file cannot be found.
        val snapshotPath = SampleCalculatorSnapshot.SNAPSHOT_FILE_PATH.resolve("i_dont_exist.jfr")
        testExtractSnapshot(
            snapshotPath,
            SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.file_not_found.message", snapshotPath),
            false,
        )
    }

    @Test
    fun testActionPerformedUnknownFormat() {
        // `extractSnapshot` should show an error dialog if the file has an unknown format.
        val snapshotPath = getResourcePath("not_a_snapshot.txt")
        testExtractSnapshot(
            snapshotPath,
            SnapvisBundle.getMessage("snapvis.actions.LoadSnapshotAction.unknown_format.message", snapshotPath.extension),
            false,
        )
    }

    private fun testExtractSnapshot(snapshotPath: Path, expectedMessage: String, shouldSucceed: Boolean) {
        val action = LoadSnapshotAction()
        expectMessageDialog(expectedMessage)
        action.extractSnapshot(project, snapshotPath)
        if (shouldSucceed) assertNotNull(metricsService.callMetrics) else assertNull(metricsService.callMetrics)
    }

    @Test
    fun testFileChooserFilter() {
        val localFileSystem = LocalFileSystem.getInstance()
        val descriptor = LoadSnapshotAction.FILE_CHOOSER_DESCRIPTOR

        val snapshotFile = localFileSystem.findFileByNioFile(SampleCalculatorSnapshot.SNAPSHOT_FILE_PATH)
        assert(descriptor.isFileSelectable(snapshotFile))

        val textFile = localFileSystem.findFileByNioFile(getResourcePath("not_a_snapshot.txt"))
        assert(!descriptor.isFileSelectable(textFile))
    }

    override fun tearDown() {
        SampleCalculatorSnapshot.clear(project)
        super.tearDown()
    }
}

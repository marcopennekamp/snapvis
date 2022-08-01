package snapvis.actions

import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TestDialogManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.test.assertNotNull

/**
 * Gets the path for a resource [name] and ensures that it exists.
 */
fun getResourcePath(name: String): Path {
    val path = object{}::class.java.classLoader.getResource(name)?.file?.let { Paths.get(it) }
    assertNotNull(path)
    assert(path.exists())
    return path
}

/**
 * Creates an empty [AnActionEvent] with [project] available from the data context.
 */
fun freshActionEvent(project: Project?): AnActionEvent = AnActionEvent.createFromDataContext(
    ActionPlaces.UNKNOWN,
    Presentation(),
    { dataId -> if (CommonDataKeys.PROJECT.`is`(dataId)) project else null },
)

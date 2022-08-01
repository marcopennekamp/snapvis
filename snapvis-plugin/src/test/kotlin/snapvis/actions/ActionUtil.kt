package snapvis.actions

import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.project.Project

fun freshActionEvent(project: Project?): AnActionEvent = AnActionEvent.createFromDataContext(
    ActionPlaces.UNKNOWN,
    Presentation(),
    { dataId -> if (CommonDataKeys.PROJECT.`is`(dataId)) project else null },
)

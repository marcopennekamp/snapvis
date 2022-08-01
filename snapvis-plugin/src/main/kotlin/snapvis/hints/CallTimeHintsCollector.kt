package snapvis.hints

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.presentation.MenuOnClickPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.codeInsight.hints.ShowInlayHintsSettings
import org.jetbrains.kotlin.idea.core.util.getLineNumber
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import snapvis.metrics.MetricsService
import snapvis.util.Nanoseconds
import snapvis.util.getContainingClassName

// TODO: Test this.

@Suppress("UnstableApiUsage")
class CallTimeHintsCollector(
    private val project: Project,
    editor: Editor,
) : FactoryInlayHintsCollector(editor) {
    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        when (element) {
            is KtCallExpression -> {
                val className = getContainingClassName(element, element.containingKtFile)
                val methodName = element.getCallNameExpression()?.getReferencedName()
                if (className != null && methodName != null) {
                    project.getService(MetricsService::class.java).callMetrics?.let { callMetrics ->
                        callMetrics.get(className).get(element.getLineNumber() + 1, methodName)?.let {
                            methodCallTime -> addHint(element, sink, methodCallTime.timePerCall)
                        }
                    }
                }
            }
            else -> { }
        }
        return true
    }

    private fun addHint(expression: KtCallExpression, sink: InlayHintsSink, timePerCall: Nanoseconds) {
        val displayedTime = "%.2f".format(timePerCall.ns / 1000000.0)

        // Don't display a hint if the reported time would be `0.00ms`. This is technically different from not
        // displaying a hint due to a complete lack of data, but that is not sufficiently apparent to the user.
        if (displayedTime == "0.00") {
            return
        }

        // Prefer to place the hint at the end of the value argument list, but before any lambda arguments (i.e.
        // trailing lambdas). Trailing lambdas often span multiple lines and are visually distinguished from the "head"
        // of a function call even with single-line use.
        val offset =
            expression.valueArgumentList?.endOffset ?:
            expression.calleeExpression?.endOffset ?:
            expression.endOffset
        val basePresentation = factory.roundWithBackgroundAndSmallInset(factory.smallText("${displayedTime}ms"))

        // Give the user direct access to the inlay hints settings with a right-click menu.
        val presentation = MenuOnClickPresentation(basePresentation, project) { listOf(ShowInlayHintsSettings()) }
        sink.addInlineElement(offset, true, presentation, false)
    }
}

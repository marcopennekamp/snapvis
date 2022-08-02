package snapvis.util

import com.intellij.util.containers.init
import org.jetbrains.kotlin.idea.util.jvmFqName
import org.jetbrains.kotlin.lombok.utils.capitalize
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * Normalizes [jvmClassName]. The aim of class name normalization is to map compiled `.class` names to Kotlin class
 * names.
 *
 * The following rules are applied:
 *
 *  - Replace `/` with `.`. The JVM uses slashes to separate packages. Also see the
 *    [JVM spec](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.2).
 *  - Remove all `$` suffixes. These classes are generated by the Kotlin compiler for lambda functions and inner
 *    classes, but the hints provider will not be aware of that. For example, `calculator.Tokenizer$tryOperand$1$2`
 *    should be normalized to `calculator.Tokenizer`.
 *
 * As mentioned in the tradeoffs (see README), this function was written with simplicity in mind and will not
 * necessarily cover all edge cases.
 */
fun normalizeClassName(jvmClassName: String): String {
    val parts = jvmClassName.replace('/', '.').split('.')
    if (parts.isEmpty()) return ""
    val normalizedSimpleName = parts.last().takeWhile { it != '$' }
    return (parts.init() + normalizedSimpleName).joinToString(".")
}

/**
 * Returns the name of the class that contains [expression]. If [expression] is contained in a package-level
 * function, the compiled class's name is returned instead, such as `UtilKt` for a file `Util.kt`.
 *
 * The package-level mapping only works for the general case, as per the project trade-offs. Annotations such as
 * `JvmName` and other edge cases are not handled explicitly.
 *
 * See also: [Package-level functions](https://kotlinlang.org/docs/java-to-kotlin-interop.html#package-level-functions).
 */
fun getContainingClassName(expression: KtExpression, file: KtFile): String? {
    val containingClass = expression.containingClass()
    return if (containingClass != null) {
        // We still have to normalize the class name to remove inner class names. For example, an inner class name
        // `com.example.Foo$Bar` should be normalized to `com.example.Foo`.
        containingClass.jvmFqName?.let { normalizeClassName(it) }
    } else {
        // The file class name is an approximation for the general case and not guaranteed to always correspond to the
        // Kotlin compiler's choice of such a class name.
        file.packageFqName.toString() + "." + file.name.removeSuffix(".kt").capitalize() + "Kt"
    }
}

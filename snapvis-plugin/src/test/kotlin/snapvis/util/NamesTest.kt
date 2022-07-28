package snapvis.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NamesTest {
    @Test
    fun `normalizeClassName normalizes JVM class names`() {
        assertEquals("calculator.CalculatorKt", normalizeClassName("calculator/CalculatorKt"))
        assertEquals("calculator.TokenizerKt", normalizeClassName("calculator/TokenizerKt"))
        assertEquals("calculator.Tokenizer", normalizeClassName("calculator/Tokenizer"))
        assertEquals("calculator.Tokenizer", normalizeClassName("calculator/Tokenizer\$accept\$1"))
        assertEquals("calculator.Tokenizer", normalizeClassName("calculator/Tokenizer\$tryOperand\$1\$2"))
    }
}

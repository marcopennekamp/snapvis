package calculator

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {
    @Test
    fun `calculate produces expected results`() {
        assertEquals(3.0, calculate("1 2 +"))
        assertEquals(-1.0, calculate("1 2 -"))
        assertEquals(7.0, calculate("1 2 3 * +"))
        assertEquals(0.5, calculate("5 30 3 / /"))
        assertEquals(8.4, calculate("0.9 1 / 2.5 3 * +"))
        assertEquals(20.0, calculate("0 1 1 2 3 5 8 ++++++"))
        assertEquals(40.0, calculate("8 5 3 2 1 1 0 +/+-+*"))
        assertEquals(-35.0, calculate("7 5 * 0 4 - 2 3 * - 9 + /"))
    }

    @Test
    fun `calculate rejects invalid expressions`() {
        // rejected by the tokenizer
        assertEquals(null, calculate("1 2 + 3 * x"))
        assertEquals(null, calculate("1.2.3 4.5 +"))

        // rejected by the evaluator (empty stack)
        assertEquals(null, calculate(""))
        assertEquals(null, calculate("+"))
        assertEquals(null, calculate("1 +"))
        assertEquals(null, calculate("1 2 + 3 - 4 * 5 / /"))
        assertEquals(null, calculate("- 2 * / 3.14159265 +"))
    }
}

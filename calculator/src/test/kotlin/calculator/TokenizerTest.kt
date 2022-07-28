package calculator

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TokenizerTest {
    @Test
    fun `tokenize produces correct token lists`() {
        assertContentEquals(listOf(), tokenize(""))

        assertContentEquals(
            listOf(
                OperandToken(1.0),
                OperandToken(2.0),
                OperatorToken(Operator.ADD),
            ),
            tokenize("1 2 +"),
        )

        assertContentEquals(
            listOf(
                OperandToken(0.9),
                OperandToken(1.0),
                OperatorToken(Operator.DIV),
                OperandToken(2.5),
                OperandToken(3.0),
                OperatorToken(Operator.MUL),
                OperatorToken(Operator.ADD),
            ),
            tokenize("0.9 1 / 2.5 3 * +"),
        )

        assertContentEquals(
            listOf(
                OperatorToken(Operator.SUB),
                OperandToken(2.0),
                OperatorToken(Operator.MUL),
                OperatorToken(Operator.DIV),
                OperandToken(3.14159265),
                OperatorToken(Operator.ADD),
            ),
            tokenize("- 2 * / 3.14159265 +"),
        )
    }

    @Test
    fun `tokenize ignores whitespace`() {
        assertContentEquals(
            listOf(),
            tokenize("             \n        \t              "),
        )

        assertContentEquals(
            listOf(
                OperandToken(1.0),
                OperandToken(2.0),
                OperatorToken(Operator.ADD),
            ),
            tokenize("   1 2       +       "),
        )

        assertContentEquals(
            listOf(
                OperandToken(1.0),
                OperandToken(2.0),
                OperatorToken(Operator.ADD),
            ),
            tokenize("       1      \n   2 \n     \t      + \n\n        "),
        )

        assertContentEquals(
            listOf(
                OperandToken(0.0),
                OperandToken(1.0),
                OperandToken(1.0),
                OperandToken(2.0),
                OperandToken(3.0),
                OperatorToken(Operator.ADD),
                OperatorToken(Operator.ADD),
                OperatorToken(Operator.ADD),
                OperatorToken(Operator.ADD),
            ),
            tokenize("0 1 1 2 3++++"),
        )
    }

    @Test
    fun `tokenize rejects invalid expressions`() {
        assertEquals(null, tokenize("1 2 + 3 * x"))
        assertEquals(null, tokenize("1.2.3 4.5 +"))
    }
}

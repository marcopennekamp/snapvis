package calculator

/**
 * An operand or operator token. When the calculator parses a string containing an RPN expression, it creates a list of
 * [Token]s, which can then be used to calculate the result of the expression.
 */
sealed interface Token

enum class Operator {
    ADD, SUB, MUL, DIV
}

val OPERATOR_MAP = mapOf(
    '+' to Operator.ADD,
    '-' to Operator.SUB,
    '*' to Operator.MUL,
    '/' to Operator.DIV,
)

data class OperandToken(val value: Double) : Token
data class OperatorToken(val operator: Operator) : Token

package calculator

/**
 * Evaluates [expression] using a stack-based evaluation strategy.
 *
 * The operand stack is manipulated based on whether an operand or operator is encountered:
 *
 *  - A number operand is placed on the top of the operand stack.
 *  - An operator consumes the two top operands on the stack and places a single result on top of the stack. The first
 *    operand on the stack is the operator's *last* operand.
 *
 * To evaluate an operator, the operand stack must contain at least two operands. If not, `null` is returned. After
 * evaluation, the result of the expression is the top of the operand stack. If the operand stack is empty after
 * evaluation, `null` is returned.
 */
fun evaluate(expression: List<Token>): Double? {
    val operandStack = ArrayDeque<Double>()

    expression.forEach { token ->
        val result = when(token) {
            is OperandToken -> token.value
            is OperatorToken -> {
                if (operandStack.size < 2) {
                    return null
                }

                val b = operandStack.removeLast()
                val a = operandStack.removeLast()
                when (token.operator) {
                    Operator.ADD -> a + b
                    Operator.SUB -> a - b
                    Operator.MUL -> a * b
                    Operator.DIV -> a / b
                }
            }
        }
        operandStack.addLast(result)
    }

    return operandStack.lastOrNull()
}

package calculator

import kotlin.random.Random

/**
 * Parses and evaluates [expression]. See [tokenize] and [evaluate] for more information.
 */
fun calculate(expression: String): Double? = tokenize(expression)?.let { evaluate(it) }

/**
 * Generates an expression with [length] operands and `[length] - 1` operators taken from [generateOperand] and [generateOperator].
 */
fun generate(length: Int, generateOperand: (Int) -> Any, generateOperator: (Int) -> Any): String {
    val builder = StringBuilder()
    builder.append(generateOperand(0))
    builder.append(" ")

    for (i in 1 until length) {
        builder.append(generateOperand(i))
        builder.append(" ")
        builder.append(generateOperator(i - 1))
        builder.append(" ")
    }

    return builder.toString()
}

private const val BENCHMARK_EXPRESSION_LENGTH = 1000000

fun main() {
    val expression1 = generate(BENCHMARK_EXPRESSION_LENGTH, { "%.2f".format(Random.nextDouble(0.0, 1000.0)) }, { "+" })
    val expression2 = generate(BENCHMARK_EXPRESSION_LENGTH, { Random.nextInt(1, 10) }, { OPERATOR_MAP.keys.random(Random) })
    val expression3 = generate(BENCHMARK_EXPRESSION_LENGTH, { "7 5 * 0 4 - 2 3 * - 9 + /" }, { "-" })

    // Benchmarking the evaluation of a few, very large expressions is preferable to evaluating many smaller
    // expressions, because it'll give the sampling profiler a good chance to accurately gauge the run time of each
    // `calculate` function.
    println(calculate(expression1))
    println(calculate(expression2))
    println(calculate(expression3))
}

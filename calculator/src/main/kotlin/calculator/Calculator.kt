package calculator

/**
 * Parses and evaluates [expression]. See [tokenize] and [evaluate] for more information.
 */
fun calculate(expression: String): Double? = tokenize(expression)?.let { evaluate(it) }

var result: Double = 0.0

fun main() {
    // TODO: Calculate a much longer expression so that the sampling rate doesn't need to be so high.
    for (i in 0..1000000) {
        result = calculate("7 5 * 0 4 - 2 3 * - 9 + /")!!
    }
    println(result)
}

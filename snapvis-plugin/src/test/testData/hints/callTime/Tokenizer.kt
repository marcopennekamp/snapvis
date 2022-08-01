package calculator

private typealias Position = Int

/**
 * Takes [input] in reverse Polish notation and parses it, producing a list of [Token]s or null if a parsing error
 * occurs.
 */
fun tokenize(input: String): List<Token>? = Tokenizer(input).tokenize()<# 2.06ms #>

/**
 * [Tokenizer] combines state that the various parsing functions need to access throughout the tokenization process.
 */
private class Tokenizer(val input: String) {

    private var position: Position = 0

    fun tokenize(): List<Token>? {
        val tokens = ArrayList<Token>()

        // Skip whitespace once before the loop so that the input may begin with whitespace.
        skipWhitespace()<# 0.01ms #>

        while (position < input.length) {
            val token = tryOperand()<# 0.48ms #> ?: tryOperator()<# 0.03ms #>
            if (token != null) {
                tokens.add(token)<# 0.01ms #>
            } else {
                // The tokenizer cannot parse the full string.
                return null
            }

            // Skipping whitespace at the end of the loop, as opposed to the beginning of the loop, makes the loop's
            // condition end tokenization if the input ends in whitespace.
            skipWhitespace()<# 0.01ms #>
        }

        return tokens
    }

    /**
     * Skips whitespace until a non-whitespace character has been reached at [position].
     */
    private fun skipWhitespace() {
        skipAll<# 0.01ms #> { it.isWhitespace() }
    }

    /**
     * Tries to parse a real-value operand.
     */
    private fun tryOperand(): OperandToken? = guardPosition<# 0.48ms #> {
        // This function uses `guardPosition` to reset `position` if the operand cannot be parsed.
        val numberBuilder = StringBuilder()

        if (acceptConsumeAll(numberBuilder::append) { it.isDigit() } > 0) {
            if (acceptConsume(numberBuilder::append) { it == '.' }) {
                // Parse the optional fractional part.
                acceptConsumeAll(numberBuilder::append) { it.isDigit() }
            }

            numberBuilder.toString().toDoubleOrNull()<# 0.21ms #>?.let { OperandToken(it) }
        } else null
    }

    /**
     * Tries to parse an arithmetic operator defined in [OPERATOR_MAP].
     */
    private fun tryOperator(): OperatorToken? = acceptMap<# 0.03ms #> { OPERATOR_MAP[it] }?.let { OperatorToken(it) }

    /**
     * Resets [position] to the position when this guard was entered if [block] returns null.
     */
    private fun <R> guardPosition(block: () -> R?): R? {
        val startPosition = position
        val result = block()
        if (result == null) {
            position = startPosition
        }
        return result
    }

    /**
     * Peeks the current character, without incrementing [position].
     */
    private fun peek(): Char? = input.getOrNull(position)<# 0.01ms #>

    /**
     * Increments [position] if [f] returns a non-null value for the current character. A character can only be
     * accepted if [position] is in the bounds of the [input]. Returns the result of [f] or null.
     */
    private fun <R> acceptMap(f: (Char) -> R?): R? = peek()<# 0.01ms #>?.let { f(it) }?.let { position += 1; it }

    /**
     * Increments [position] if the current character is accepted by [predicate]. A character can only be accepted if
     * [position] is in the bounds of the [input]. Returns the accepted character or null.
     */
    private fun accept(predicate: (Char) -> Boolean): Char? = acceptMap<# 0.01ms #> { if (predicate(it)) it else null }

    /**
     * Consumes the current character with [consume] if the character is accepted by [predicate]. Returns whether the
     * character was accepted and consumed.
     */
    private fun acceptConsume(consume: (Char) -> Unit, predicate: (Char) -> Boolean): Boolean {
        return accept(predicate)<# 0.01ms #>?.let { consume(it) } != null
    }

    /**
     * Consumes the current character with [consume] as long as the character is accepted by [predicate]. Returns the
     * number of characters that were consumed.
     */
    private fun acceptConsumeAll(consume: (Char) -> Unit, predicate: (Char) -> Boolean): Int {
        var count = 0
        while (acceptConsume(consume, predicate)<# 0.01ms #>) {
            count += 1
        }
        return count
    }

    private fun skip(predicate: (Char) -> Boolean): Boolean = accept(predicate)<# 0.01ms #> != null

    private fun skipAll(predicate: (Char) -> Boolean) {
        while (skip(predicate)<# 0.01ms #>) {
            // ignore
        }
    }

}

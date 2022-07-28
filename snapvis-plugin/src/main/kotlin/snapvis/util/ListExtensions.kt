package snapvis.util

import kotlin.math.min

/**
 * Returns the size of the common prefix which [this] and [other] share. For example, the common prefix size of two
 * lists `listOf(1, 2, 3, 4)` and `listOf(1, 2, 5, 4)` is 2.
 */
fun <A> List<A>.commonPrefixSize(other: List<A>): Int {
    var result = 0
    val safeSize = min(this.size, other.size)
    for (index in 0 until safeSize) {
        if (this[index] == other[index]) result += 1 else break
    }
    return result
}

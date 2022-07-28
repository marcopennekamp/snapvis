package snapvis.util

import kotlin.test.Test
import kotlin.test.assertEquals

class ListExtensionsTest {
    @Test
    fun `commonPrefixSize calculates the common prefix size of two lists`() {
        fun <T> assertPrefixSize(expectedSize: Int, list1: List<T>, list2: List<T>) {
            assertEquals(expectedSize, list1.commonPrefixSize(list2))
        }

        assertPrefixSize(0, listOf<Int>(), listOf())
        assertPrefixSize(0, listOf(1, 2, 3), listOf(3, 2, 1))
        assertPrefixSize(4, listOf(1, 2, 3, 4), listOf(1, 2, 3, 4))
        assertPrefixSize(2, listOf(1, 2, 3, 4), listOf(1, 2, 5, 4))
        assertPrefixSize(2, listOf(1, 2, 3, 4), listOf(1, 2))
        assertPrefixSize(2, listOf(1, 2), listOf(1, 2, 3, 4))

        assertPrefixSize(1, listOf("", ""), listOf(""))
        assertPrefixSize(1, listOf("hello", "world"), listOf("hello", "prefix"))
        assertPrefixSize(2, listOf("hello", "world"), listOf("hello", "world"))
        assertPrefixSize(0, listOf("hello", "world"), listOf("world", "hello"))
    }
}

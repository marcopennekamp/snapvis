package snapvis.util

@JvmInline
value class Nanoseconds(val ns: Long) {
    operator fun plus(other: Nanoseconds): Nanoseconds = Nanoseconds(ns + other.ns)
    operator fun div(other: Int): Nanoseconds = Nanoseconds(ns / other)
}

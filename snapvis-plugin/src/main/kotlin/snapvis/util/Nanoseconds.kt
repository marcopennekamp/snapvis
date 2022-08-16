package snapvis.util

@JvmInline
value class Nanoseconds(val ns: Long) {
    operator fun plus(other: Nanoseconds): Nanoseconds = (ns + other.ns).ns
    operator fun div(other: Int): Nanoseconds = (ns / other).ns

    val toMilliseconds: Double
        get() = this.ns / 1_000_000.0
}

val Int.ns: Nanoseconds
    get() = Nanoseconds(this.toLong())

val Long.ns: Nanoseconds
    get() = Nanoseconds(this)

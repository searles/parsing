package at.searles.parsing.lexer.fsa

data class Interval(val range: IntRange) /*: Comparable<Interval>*/ {
    constructor(ch: Int): this(ch .. ch)
    constructor(start: Int, end: Int): this(start until end)

    val first = range.first
    val last = range.last

    operator fun contains(ch: Int): Boolean {
        return ch in range
    }

    override fun toString(): String {
        return range.toString()
    }

    companion object {
        val all by lazy {
            Interval(Int.MIN_VALUE until Int.MAX_VALUE) // TODO
        }
    }
}
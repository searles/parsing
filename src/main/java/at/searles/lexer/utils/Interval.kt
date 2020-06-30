package at.searles.lexer.utils

data class Interval(val start: Int, val end: Int) : Comparable<Interval> {
    constructor(ch: Int): this(ch, ch + 1)

    init {
        require(start < end)
    }

    override fun compareTo(other: Interval): Int {
        // Lexical order
        val cmp = start.compareTo(other.start)
        return if (cmp != 0) cmp else end.compareTo(other.end)
    }

    operator fun contains(ch: Int): Boolean {
        return ch in start until end
    }

    override fun toString(): String {
        return "[$start, $end)"
    }

    companion object {
        val all by lazy {
            Interval(Int.MIN_VALUE, Int.MAX_VALUE)
        }
    }
}
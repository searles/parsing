package at.searles.regexp

/**
 * Strictly speaking not a regex, but a
 * good and efficient replacement for non-greedy.
 */
internal class FirstMatch(private val t: Regexp) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitFirstMatch(t)
    }

    override fun toString(): String {
        return String.format("nonGreedy(%s)", t)
    }

}
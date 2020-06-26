package at.searles.regexp

/**
 * Strictly speaking not a regex, but a
 * good and efficient replacement for non-greedy.
 */
internal class NonGreedy(private val t: Regexp) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitNonGreedy(t)
    }

    override fun toString(): String {
        return String.format("nonGreedy(%s)", t)
    }

}
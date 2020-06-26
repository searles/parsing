package at.searles.regexp

internal class RepRange(private val t: Regexp, private val min: Int, private val max: Int) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitRepRange(t, min, max)
    }

    override fun toString(): String {
        return String.format("range(%s, %d, %d)", t, min, max)
    }

}
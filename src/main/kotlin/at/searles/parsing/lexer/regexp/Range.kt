package at.searles.parsing.lexer.regexp

internal class Range(private val t: Regexp, private val min: Int, private val max: Int) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitRange(t, min, max)
    }

    override fun toString(): String {
        return String.format("range(%s, %d, %d)", t, min, max)
    }

}
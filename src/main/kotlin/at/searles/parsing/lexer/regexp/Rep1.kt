package at.searles.parsing.lexer.regexp

internal class Rep1(private val t: Regexp) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitKleenePlus(t)
    }

    override fun toString(): String {
        return String.format("(%s)+", t)
    }
}
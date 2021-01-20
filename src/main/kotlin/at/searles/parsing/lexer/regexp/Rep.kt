package at.searles.parsing.lexer.regexp

internal class Rep(private val t: Regexp) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitRep(t)
    }

    override fun toString(): String {
        return String.format("rep(%s)", t)
    }

}
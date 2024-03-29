package at.searles.parsing.lexer.regexp

internal class Intersect(private val l: Regexp, private val r: Regexp) : Regexp {
    override fun toString(): String {
        return "$l & $r"
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitAnd(l, r)
    }

}
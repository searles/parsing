package at.searles.parsing.lexer.regexp

internal class Concat(private val l: Regexp, private val r: Regexp) : Regexp {
    fun l(): Regexp {
        return l
    }

    fun r(): Regexp {
        return r
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitThen(l, r)
    }

    override fun toString(): String {
        return String.format("then(%s, %s)", l, r)
    }

}
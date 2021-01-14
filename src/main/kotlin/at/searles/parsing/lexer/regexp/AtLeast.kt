package at.searles.parsing.lexer.regexp

internal class AtLeast(private val regexp: Regexp, private val count: Int) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitAtLeast(regexp, count)
    }

    override fun toString(): String {
        return String.format("min(%s, %d)", regexp, count)
    }

}
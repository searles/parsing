package at.searles.regexp

internal class Opt(private val t: Regexp) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitOpt(t)
    }

    override fun toString(): String {
        return String.format("(%s)?", t)
    }
}
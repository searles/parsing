package at.searles.regexp

internal class RepCount(private val regexp: Regexp, private val count: Int) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitRepCount(regexp, count)
    }

    override fun toString(): String {
        return String.format("count(%s, %d)", regexp, count)
    }

}
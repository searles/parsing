package at.searles.regexp

internal class RepMin(private val regexp: Regexp, private val min: Int) : Regexp {
    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitRepMin(regexp, min)
    }

    override fun toString(): String {
        return String.format("min(%s, %d)", regexp, min)
    }

}
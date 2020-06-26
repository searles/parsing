package at.searles.regexp

class Text(seq: CharSequence) : Regexp {
    private val string: String = seq.toString()

    override fun toString(): String {
        return string
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitText(string)
    }

}
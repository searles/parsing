package at.searles.parsing.lexer.regexp

class Text(seq: CharSequence) : Regexp {
    val string: String = seq.toString()

    override fun toString(): String {
        return string
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitText(string)
    }

    companion object {
        fun many(vararg seqs: CharSequence): Regexp {
            return seqs.drop(1).fold(Text(seqs.first()) as Regexp, { a, b -> a or Text(b) } )
        }

        fun itext(seq: CharSequence): Regexp {
            return seq.map { CharSet.ichars(it) }.reduce { acc, charSet -> acc union charSet }
        }

        fun imany(vararg seqs: CharSequence): Regexp {
            return seqs.map { itext(it) }.reduce { a, b -> a or b }
        }
    }
}
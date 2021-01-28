package at.searles.parsing.lexer.regexp

import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.codepoint.StringCodePointStream

class Text(seq: CharSequence) : Regexp {
    val string: String = seq.toString()

    override fun toString(): String {
        return string
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitText(string)
    }

    companion object {
        fun itext(seq: CharSequence): Regexp {
            val stream = StringCodePointStream(seq.toString())
            var regexp: Regexp = CharSet.ichars(stream.read())

            while(true) {
                val ch = stream.read()
                if(ch == -1) break
                regexp += CharSet.ichars(ch)

            }

            return regexp
        }

        fun many(vararg seqs: CharSequence): Regexp {
            return seqs.map<CharSequence, Regexp> {
                Text(it.toString())
            }.reduce { acc, charSet ->
                acc or charSet
            }
        }

        fun imany(vararg seqs: CharSequence): Regexp {
            return seqs.map { itext(it) }.reduce { a, b -> a or b }
        }
    }
}
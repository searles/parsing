package at.searles.parsingtools.common

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

object ToString : Mapping<CharSequence, String> {
    override fun reduce(left: CharSequence, stream: ParserStream): String {
        return left.toString()
    }

    override fun left(result: String): CharSequence {
        return result
    }

    override fun toString(): String {
        return "{x -> x.toString}"
    }
}

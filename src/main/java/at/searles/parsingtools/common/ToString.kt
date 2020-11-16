package at.searles.parsingtools.common

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

object ToString : Mapping<CharSequence, String> {
    override fun parse(stream: ParserStream, input: CharSequence): String {
        return input.toString()
    }

    override fun left(result: String): CharSequence? {
        return result
    }

    override fun toString(): String {
        return "toString"
    }
}

package at.searles.parsingtools.common

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

object ToInt : Mapping<CharSequence, Int> {
    override fun parse(stream: ParserStream, input: CharSequence): Int {
        try {
            return Integer.parseInt(input.toString())
        } catch (e: NumberFormatException) {
            error("input was not an int number")
        }

    }

    override fun left(result: Int): CharSequence {
        return result.toString()
    }
}

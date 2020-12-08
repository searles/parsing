package at.searles.parsingtools.common

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

object ToInt : Mapping<CharSequence, Int> {
    override fun parse(left: CharSequence, stream: ParserStream): Int {
        try {
            return Integer.parseInt(left.toString())
        } catch (e: NumberFormatException) {
            error("input was not an int number")
        }
    }

    override fun left(result: Int): CharSequence {
        return result.toString()
    }
}

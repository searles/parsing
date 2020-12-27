package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

/**
 * Append codePoint (!) to String.
 */
object StringAppender : Fold<String, String, String> {
    override fun apply(stream: ParserStream, left: String, right: String): String {
        return left + right
    }

    override fun toString(): String {
        return "{string + string}"
    }
}

package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

/**
 * Append codePoint (!) to String.
 */
class AppendToString : Fold<String, Int, String> {
    override fun apply(stream: ParserStream, left: String, right: Int): String {
        return left + String(Character.toChars(right))
    }

    override fun leftInverse(result: String): String? {
        if (result.isEmpty()) {
            return null
        }

        return if (Character.isHighSurrogate(result[result.length - 1])) {
            result.substring(0, result.length - 2)
        } else {
            result.substring(0, result.length - 1)
        }
    }

    override fun rightInverse(result: String): Int? {
        return if (result.isEmpty()) {
            null
        } else result.codePointAt(result.length - 1)

    }

    override fun toString(): String {
        return "{string + codepoint}"
    }
}

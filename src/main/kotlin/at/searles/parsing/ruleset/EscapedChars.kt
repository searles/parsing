package at.searles.parsing.ruleset


import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.Conversion

object EscapedChars {
    val hexDigit = CharSet('0'..'9', 'a'..'f', 'A'..'F')

    fun hexToInt(value: CharSequence): Int {
        return value.toString().substring(2).toInt(16)
    }

    val specialChar = Text("\\") + (
            CharSet('n', 'r', 't') or
            Text("x") + hexDigit.count(2) or
            Text("u") + hexDigit.count(4) or
            Text("U") + hexDigit.count(8)
    )

    object CreateSpecialChar: Conversion<String, Int> {
        override fun convert(value: String): Int {
            return when(value[1]) {
                'n' -> '\n'.toInt()
                'r' -> '\r'.toInt()
                't' -> '\t'.toInt()
                'x', 'u', 'U' -> hexToInt(value)
                else -> error("unexpected char: $value")
            }
        }
    }

    val regularChar = Text("\\") + CharSet.all() or CharSet.all()

    object CreateRegularChar: Conversion<String, Int> {
        override fun convert(value: String): Int {
            return if(value[0] == '\\' && value.length > 1) {
                value.codePointAt(1)
            } else {
                value.codePointAt(0)
            }
        }
    }
}
package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult

object AsInt: Conversion<CharSequence, Int> {
    override fun convert(value: CharSequence): Int {
        return value.toString().toInt()
    }

    override fun invert(value: Int): FnResult<CharSequence> {
        return FnResult.success(value.toString())
    }

    override fun toString(): String {
        return "{asInt}"
    }
}
package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult

object AsString: Conversion<CharSequence, String> {
    override fun convert(value: CharSequence): String {
        return value.toString()
    }

    override fun invert(value: String): FnResult<CharSequence> {
        return FnResult.success(value)
    }
}
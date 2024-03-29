package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.printer.PrintTree

class ParserPlusConversion<A, B>(private val parser: Parser<A>, private val conversion: Conversion<A, B>) : Parser<B> {
    override fun parse(stream: TokenStream): ParserResult<B> {
        val result = parser.parse(stream)

        if(!result.isSuccess) {
            return ParserResult.failure
        }

        return ParserResult.of(conversion.convert(result.value), result.startIndex, result.endIndex)
    }

    override fun print(value: B): PrintTree {
        val inverted = conversion.invert(value)

        if(!inverted.isSuccess) {
            return PrintTree.failure
        }

        return parser.print(inverted.value)
    }
}

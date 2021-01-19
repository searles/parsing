package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.PrintResult

class ParserUnion<A>(private val parsers: List<Parser<A>>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        for(parser in parsers) {
            val result = parser.parse(stream)
            if(result.isSuccess) {
                return result
            }
        }

        return ParserResult.failure()
    }

    override fun print(value: A): PrintResult {
        for(parser in parsers) {
            val result = parser.print(value)

            if(result.isSuccess) {
                return result
            }
        }

        return PrintResult.failure()
    }

    override fun or(other: Parser<A>): Parser<A> {
        return ParserUnion(parsers + other)
    }
}

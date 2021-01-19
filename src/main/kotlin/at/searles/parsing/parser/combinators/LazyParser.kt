package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.PrintResult

class LazyParser<A>(private val parser: () -> Parser<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        return parser().parse(stream)
    }

    override fun print(value: A): PrintResult {
        return parser().print(value)
    }
}

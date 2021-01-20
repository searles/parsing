package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.PrintTree

class LazyParser<A> constructor(_parser: Parser<A>? = null): Parser<A> {
    lateinit var parser: Parser<A>

    init {
        if(_parser != null) {
            this.parser = _parser
        }
    }

    override fun parse(stream: ParserStream): ParserResult<A> {
        return parser.parse(stream)
    }

    override fun print(value: A): PrintTree {
        return parser.print(value)
    }
}

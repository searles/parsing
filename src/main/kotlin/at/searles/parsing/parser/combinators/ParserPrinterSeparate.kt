package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

class ParserPrinterSeparate<A>(private val parser: Parser<A>, private val printer: Parser<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        return parser.parse(stream)
    }

    override fun print(value: A): PrintTree {
        return printer.print(value)
    }
}

package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree

class ReducerPrinterSeparate<A>(private val parser: Reducer<A, A>, private val printer: Reducer<A, A>) : Reducer<A, A> {
    override fun parse(stream: ParserStream, input: A): ParserResult<A> {
        return parser.parse(stream, input)
    }

    override fun print(value: A): PartialPrintTree<A> {
        return printer.print(value)
    }

    override fun toString(): String {
        return "*$parser"
    }

}

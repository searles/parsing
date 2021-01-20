package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintResult
import at.searles.parsing.printer.PrintTree

class OptionalReducer<A>(private val reducer: Reducer<A, A>): Reducer<A, A> {
    override fun parse(stream: ParserStream, input: A): ParserResult<A> {
        val result = reducer.parse(stream, input)

        if(result.isSuccess) {
            return result
        }

        return ParserResult.success(input, stream.index, 0)
    }

    override fun print(value: A): PartialPrintResult<A> {
        val result = reducer.print(value)

        if(result.isSuccess) {
            return result
        }

        return PartialPrintResult.success(value, PrintTree.Empty)
    }
}

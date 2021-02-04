package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

class OptionalReducer<A>(private val reducer: Reducer<A, A>): Reducer<A, A> {
    override fun parse(stream: ParserStream, input: A): ParserResult<A> {
        val result = reducer.parse(stream, input)

        if(result.isSuccess) {
            return result
        }

        return ParserResult.of(input, stream.index, 0)
    }

    override fun print(value: A): PartialPrintTree<A> {
        val result = reducer.print(value)

        if(result.isSuccess) {
            return result
        }

        return PartialPrintTree.of(value, PrintTree.Empty)
    }

    override fun toString(): String {
        return "$reducer.opt()"
    }
}

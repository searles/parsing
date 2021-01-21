package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree

class ReducerPlusReducer<A, B, C>(private val left: Reducer<A, B>, private val right: Reducer<B, C>) : Reducer<A, C> {
    override fun parse(stream: ParserStream, input: A): ParserResult<C> {
        val leftResult = left.parse(stream, input)

        if(!leftResult.isSuccess) return ParserResult.failure

        val rightResult = right.parse(stream, leftResult.value)

        if(!rightResult.isSuccess) {
            stream.backtrackToIndex(leftResult.index)
            return ParserResult.failure
        }

        return ParserResult.of(rightResult.value, leftResult.index, stream.index - leftResult.index)
    }

    override fun print(value: C): PartialPrintTree<A> {
        TODO("Not yet implemented")
    }

    // TODO plus!

}

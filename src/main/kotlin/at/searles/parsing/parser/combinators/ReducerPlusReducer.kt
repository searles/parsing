package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree

class ReducerPlusReducer<A, B, C>(private val left: Reducer<A, B>, private val right: Reducer<B, C>) : Reducer<A, C> {
    override fun parse(stream: TokenStream, input: A): ParserResult<C> {
        val state0 = stream.getState()
        val leftResult = left.parse(stream, input)

        if(!leftResult.isSuccess) return ParserResult.failure

        val rightResult = right.parse(stream, leftResult.value)

        if(!rightResult.isSuccess) {
            stream.restoreState(state0)
            return ParserResult.failure
        }

        return ParserResult.of(rightResult.value, leftResult.startIndex, rightResult.endIndex)
    }

    override fun print(value: C): PartialPrintTree<A> {
        val rightResult = right.print(value)

        if(!rightResult.isSuccess) return PartialPrintTree.failure

        val leftResult = left.print(rightResult.leftValue)

        if(!leftResult.isSuccess) return PartialPrintTree.failure

        return PartialPrintTree.of(leftResult.leftValue, leftResult.rightTree + rightResult.rightTree)
    }

    override operator fun <D> plus(reducer: Reducer<C, D>): Reducer<A, D> {
        return left + (right + reducer)
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }
}

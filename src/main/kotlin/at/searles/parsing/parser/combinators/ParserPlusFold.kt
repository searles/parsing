package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.*
import at.searles.parsing.printer.PartialPrintTree

class ParserPlusFold<A, B, C>(private val mid: Parser<A>, private val right: Fold<B, A, C>) : Reducer<B, C> {
    override fun parse(stream: ParserStream, input: B): ParserResult<C> {
        val leftResult = mid.parse(stream)

        if(!leftResult.isSuccess) {
            return ParserResult.failure
        }

        val rightValue = right.fold(input, leftResult.value)
        return ParserResult.of(rightValue, leftResult.index, leftResult.length)
    }

    override fun print(value: C): PartialPrintTree<B> {
        val leftResult = right.invertLeft(value)
        val rightResult = right.invertRight(value)

        if(!leftResult.isSuccess || !rightResult.isSuccess) {
            return PartialPrintTree.failure
        }

        val midResult = mid.print(rightResult.value)

        if(!midResult.isSuccess) {
            return PartialPrintTree.failure
        }

        return PartialPrintTree.of(leftResult.value, midResult)
    }

    override fun toString(): String {
        return "$mid.plus($right)"
    }
}

package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.*
import at.searles.parsing.printer.PartialPrintResult

class ParserPlusFold<A, B, C>(private val mid: Parser<A>, private val right: Fold<B, A, C>) : Reducer<B, C> {
    override fun parse(stream: ParserStream, input: B): ParserResult<C> {
        val leftResult = mid.parse(stream)

        if(!leftResult.isSuccess) {
            return ParserResult.failure()
        }

        val rightValue = right.fold(input, leftResult.value)
        return ParserResult.success(rightValue, leftResult.index, leftResult.length)
    }

    override fun print(value: C): PartialPrintResult<B> {
        val leftResult = right.invertLeft(value)
        val rightResult = right.invertRight(value)

        if(!leftResult.isSuccess || !rightResult.isSuccess) {
            return PartialPrintResult.failure()
        }

        val midResult = mid.print(rightResult.value)

        if(!midResult.isSuccess) {
            return PartialPrintResult.failure()
        }

        return PartialPrintResult.success(leftResult.value, midResult.output)
    }
}

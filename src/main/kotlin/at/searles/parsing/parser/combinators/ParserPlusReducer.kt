package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PrintTree

class ParserPlusReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(stream: ParserStream): ParserResult<B> {
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return ParserResult.failure

        val rightResult = right.parse(stream, leftResult.value)

        if(!rightResult.isSuccess) {
            stream.backtrackToIndex(leftResult.index)
            return ParserResult.failure
        }

        return ParserResult.of(rightResult.value, leftResult.index, stream.index - leftResult.index)
    }

    override fun print(value: B): PrintTree {
        val rightPrintResult = right.print(value)

        if(!rightPrintResult.isSuccess) {
            return PrintTree.failure
        }

        val leftPrintResult = left.print(rightPrintResult.leftValue)

        if(!leftPrintResult.isSuccess) {
            return PrintTree.failure
        }

        return leftPrintResult + rightPrintResult.rightTree
    }
}
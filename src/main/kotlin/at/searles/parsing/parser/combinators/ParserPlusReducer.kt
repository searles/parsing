package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PrintResult

class ParserPlusReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(stream: ParserStream): ParserResult<B> {
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) {
            return ParserResult.failure()
        }

        val rightResult = right.parse(stream, leftResult.value)

        if(!rightResult.isSuccess) {
            return ParserResult.failure()
        }

        val length = (rightResult.index - leftResult.index).toInt() + rightResult.length
        return ParserResult.success(rightResult.value,
            leftResult.index,
            length
        )
    }

    override fun print(value: B): PrintResult {
        val rightPrintResult = right.print(value)

        if(!rightPrintResult.isSuccess) {
            return PrintResult.failure()
        }

        val leftPrintResult = left.print(rightPrintResult.value)

        if(!leftPrintResult.isSuccess) {
            return PrintResult.failure()
        }

        return PrintResult.success(leftPrintResult.output + rightPrintResult.output)
    }
}
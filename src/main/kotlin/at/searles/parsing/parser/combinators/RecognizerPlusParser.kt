package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.printer.PrintTree

class RecognizerPlusParser<A>(private val left: Recognizer, private val right: Parser<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return ParserResult.failure

        val rightResult = right.parse(stream)

        if(!rightResult.isSuccess) {
            stream.backtrackToIndex(leftResult.index)
            return ParserResult.failure
        }

        return ParserResult.of(rightResult.value, leftResult.index, stream.index - leftResult.index)

    }

    override fun print(value: A): PrintTree {
        val rightResult = right.print(value)

        if(!rightResult.isSuccess) {
            return PrintTree.failure
        }

        return left.print() + rightResult
    }
}

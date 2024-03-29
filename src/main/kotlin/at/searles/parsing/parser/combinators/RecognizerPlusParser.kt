package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.printer.PrintTree

class RecognizerPlusParser<A>(private val left: Recognizer, private val right: Parser<A>) : Parser<A> {
    override fun parse(stream: TokenStream): ParserResult<A> {
        val state0 = stream.getState()
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return ParserResult.failure

        val rightResult = right.parse(stream)

        if(!rightResult.isSuccess) {
            stream.restoreState(state0)
            return ParserResult.failure
        }

        return ParserResult.of(rightResult.value, leftResult.index, stream.startIndex - leftResult.index)
    }

    override fun print(value: A): PrintTree {
        val rightResult = right.print(value)

        if(!rightResult.isSuccess) {
            return PrintTree.failure
        }

        return left.print() + rightResult
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }
}

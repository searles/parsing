package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PrintTree

class ParserPlusReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(stream: TokenStream): ParserResult<B> {
        val index0 = stream.startIndex
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return ParserResult.failure

        val rightResult = right.parse(stream, leftResult.value)

        if(!rightResult.isSuccess) {
            stream.restoreIndex(index0)
            return ParserResult.failure
        }

        return ParserResult.of(rightResult.value, leftResult.startIndex, rightResult.endIndex)
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

    override fun <C> plus(reducer: Reducer<B, C>): Parser<C> {
        return this.left + (this.right + reducer)
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }
}
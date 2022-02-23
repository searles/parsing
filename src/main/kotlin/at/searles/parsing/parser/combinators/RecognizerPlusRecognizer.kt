package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree

class RecognizerPlusRecognizer(private val left: Recognizer, private val right: Recognizer) : Recognizer {
    override fun parse(stream: TokenStream): RecognizerResult {
        val index0 = stream.startIndex
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return RecognizerResult.failure

        val rightResult = right.parse(stream)

        if(!rightResult.isSuccess) {
            stream.restoreIndex(index0)
            return RecognizerResult.failure
        }

        return RecognizerResult.of(leftResult.index, stream.startIndex - leftResult.index)
    }

    override fun print(): PrintTree {
        return left.print() + right.print()
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }
}

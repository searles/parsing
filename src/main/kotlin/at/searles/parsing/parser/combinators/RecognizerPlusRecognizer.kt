package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree

class RecognizerPlusRecognizer(private val left: Recognizer, private val right: Recognizer) : Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return RecognizerResult.failure

        val rightResult = right.parse(stream)

        if(!rightResult.isSuccess) {
            stream.backtrackToIndex(leftResult.index)
            return RecognizerResult.failure
        }

        return RecognizerResult.of(leftResult.index, stream.index - leftResult.index)

    }

    override fun print(): PrintTree {
        return left.print() + right.print()
    }

}

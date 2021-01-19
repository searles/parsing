package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult

class RecognizerPlusRecognizer(private val left: Recognizer, private val right: Recognizer) : Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) return RecognizerResult.failure()

        val rightResult = right.parse(stream)

        if(!rightResult.isSuccess) return RecognizerResult.failure()

        val length = (rightResult.index - leftResult.index).toInt() + rightResult.length

        return RecognizerResult.success(leftResult.index, length)
    }

    override val output: String
        get() = left.output + right.output

}

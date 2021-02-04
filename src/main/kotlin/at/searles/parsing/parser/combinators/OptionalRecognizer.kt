package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree

class OptionalRecognizer(private val recognizer: Recognizer) : Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        val result = recognizer.parse(stream)

        if(!result.isSuccess) {
            return RecognizerResult.of(stream.index, 0)
        }

        return result
    }

    override fun print(): PrintTree {
        return PrintTree.empty
    }

    override fun toString(): String {
        return "$recognizer.opt()"
    }
}

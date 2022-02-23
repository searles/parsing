package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree

class OptionalRecognizer(private val recognizer: Recognizer) : Recognizer {
    override fun parse(stream: TokenStream): RecognizerResult {
        val result = recognizer.parse(stream)

        if(!result.isSuccess) {
            return RecognizerResult.of(stream.startIndex, 0)
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

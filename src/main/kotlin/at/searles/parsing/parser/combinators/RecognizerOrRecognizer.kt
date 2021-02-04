package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree

class RecognizerOrRecognizer(private val first: Recognizer, private val second: Recognizer) : Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        val result = first.parse(stream)
        if(result.isSuccess) return result
        return second.parse(stream)
    }

    override fun print(): PrintTree {
        return first.print()
    }

    override fun toString(): String {
        return "$first.or($second)"
    }
}

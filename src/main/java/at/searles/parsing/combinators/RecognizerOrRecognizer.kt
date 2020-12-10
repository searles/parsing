package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerOrRecognizer(private val choice0: Recognizer, private val choice1: Recognizer) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(choice0, true) || stream.recognize(choice1, true)
    }

    override fun print(): ConcreteSyntaxTree {
        return choice0.print()
    }

    override fun toString(): String {
        return "$choice0.or($choice1)"
    }
}
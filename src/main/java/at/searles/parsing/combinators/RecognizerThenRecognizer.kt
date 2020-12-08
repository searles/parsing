package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerThenRecognizer(private val left: Recognizer, private val right: Recognizer): Recognizer {

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(left, true) && stream.recognize(right, false)
    }

    override fun print(): ConcreteSyntaxTree {
        return left.print().consRight(right.print())
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }

}
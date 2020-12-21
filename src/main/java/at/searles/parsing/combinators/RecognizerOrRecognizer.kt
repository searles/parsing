package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerOrRecognizer(private vararg val choices: Recognizer) : Recognizer {
    override fun or(other: Recognizer): Recognizer {
        return RecognizerOrRecognizer(*choices, other)
    }

    override fun recognize(stream: ParserStream): Boolean {
        for(choice in choices) {
            if(stream.recognize(choice, true)) {
                return true
            }
        }

        return false
    }

    override fun print(): ConcreteSyntaxTree {
        return choices.first().print()
    }

    override fun toString(): String {
        return "${choices.first()}.or(${choices.drop(1).joinToString(", ")})"
    }
}
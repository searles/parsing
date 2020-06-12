package at.searles.parsing

import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerRef(private val parent: Recognizer, private val label: String) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        return parent.recognize(stream)
    }

    override fun print(): ConcreteSyntaxTree {
        return parent.print()
    }

    override fun toString(): String {
        return label
    }

}
package at.searles.parsingtools.common

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree

class BreakPoint(val fn: () -> Unit): Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        fn()
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        fn()
        return EmptyTree
    }
}
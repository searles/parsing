package at.searles.parsing.parser.tools

import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.printer.SelectPrintTree

class Mark(private val label: Any): Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        stream.notifyMark(label)
        return RecognizerResult.of(stream.index, 0)
    }

    override fun print(): PrintTree {
        return SelectPrintTree(label)
    }

    override fun toString(): String {
        return ""
    }
}
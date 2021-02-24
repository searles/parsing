package at.searles.parsing.parser.tools

import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.OutStream
import at.searles.parsing.printer.PrintTree

class PrintInject(private val printOutput: (OutStream) -> Unit): Recognizer {

    private val tree = OutputPrintTree()

    override fun parse(stream: ParserStream): RecognizerResult {
        return RecognizerResult.of(stream.index, 0)
    }

    override fun print(): PrintTree {
        return tree
    }

    inner class OutputPrintTree: PrintTree {
        override fun print(outStream: OutStream) {
            printOutput(outStream)
        }

        override fun toString(): String {
            return ""
        }
    }

    override fun toString(): String {
        return "[]"
    }
}
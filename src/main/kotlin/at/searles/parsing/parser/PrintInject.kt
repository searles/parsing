package at.searles.parsing.parser

import at.searles.parsing.printer.OutStream
import at.searles.parsing.printer.PrintTree

class PrintInject(private val printOutput: (OutStream) -> Unit): Recognizer {

    private val tree = OutputPrintTree()

    override fun parse(stream: ParserStream): RecognizerResult {
        return RecognizerResult.success(stream.index, 0)
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
}
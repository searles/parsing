package at.searles.parsing.parser.format

import at.searles.parsing.printer.OutStream
import at.searles.parsing.printer.PrintTree

class IndentOutStream(private val outStream: OutStream): OutStream {
    private var indentLevel = 0
    private var requireIndent = false

    private fun checkIndent() {
        if(requireIndent) {
            requireIndent = false
            repeat(indentLevel) { append("    ") }
        }
    }

    override fun append(seq: CharSequence) {
        checkIndent()
        outStream.append(seq)
    }

    override fun append(codePoint: Int) {
        checkIndent()
        outStream.append(codePoint)
    }

    override fun mark(label: Any) {
        when (label) {
            "newLine" -> {
                append("\n")
                requireIndent = true
            }
            "separator" -> append(" ")
        }
    }

    override fun select(label: Any, tree: PrintTree) {
        when(label) {
            "indent" -> {
                indentLevel++
                tree.print(this)
                indentLevel--
            }
        }
    }
}
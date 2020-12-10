package at.searles.parsing.format

import at.searles.parsing.printing.CstVisitor
import at.searles.parsing.printing.OutStream

/**
 * A printwriter for concrete syntax trees. Extend this class for
 * more complex formattings.
 */
class CodePrinter(private val outStream: OutStream): CstVisitor, Printer {
    private var indentLevel = 0
    private var atBeginningOfLine = false

    override fun visitMarker(marker: Any) {
        when (marker) {
            Markers.Indent -> indentLevel ++
            Markers.Unindent -> indentLevel --
            Markers.NewLine -> {
                print(newLine)
                atBeginningOfLine = true
            }
            Markers.EmptyLine -> {
                print(emptyLine)
                atBeginningOfLine = true
            }
            Markers.Space -> {
                print(space)
            }
        }
    }

    /**
     * Print a concrete char sequence. Override this method if you want to apply
     * special formatting rules based on the current state of this printer. For instance,
     * if the beginning of a line should be indented, this method should check
     * whether it is at the beginning of a line.
     */
    override fun visitToken(seq: CharSequence) {
        if(atBeginningOfLine) {
            print(indentation.repeat(indentLevel))
            atBeginningOfLine = false
        }

        print(seq)
    }

    /**
     * Raw-print into the underlying outStream.
     */
    override fun print(seq: CharSequence) {
        outStream.append(seq)
    }

    companion object {
        const val indentation = "  "
        const val emptyLine = "\n\n"
        const val newLine = "\n"
        const val space = " "
    }
}
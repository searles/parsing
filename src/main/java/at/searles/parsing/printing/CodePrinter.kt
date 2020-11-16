package at.searles.parsing.printing

import at.searles.parsing.format.CodeFormatContext
import at.searles.parsing.format.FormatRules
import at.searles.parsing.format.Printer

/**
 * A printwriter for concrete syntax trees. Extend this class for
 * more complex formattings.
 */
class CodePrinter(rules: FormatRules, private val outStream: OutStream): CstVisitor, Printer {
    private val codeFormatter = CodeFormatContext(rules, this)

    override fun visitFormat(marker: Any) {
        codeFormatter.format(marker)
    }

    /**
     * Print a concrete char sequence. Override this method if you want to apply
     * special formatting rules based on the current state of this printer. For instance,
     * if the beginning of a line should be indented, this method should check
     * whether it is at the beginning of a line.
     */
    override fun visitToken(seq: CharSequence) {
        codeFormatter.applyFormatting()
        print(seq)
    }

    /**
     * Raw-print into the underlying outStream.
     */
    override fun print(seq: CharSequence) {
        outStream.append(seq)
    }
}
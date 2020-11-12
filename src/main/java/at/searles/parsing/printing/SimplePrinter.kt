package at.searles.parsing.printing

/**
 * A printwriter for concrete syntax trees. Extend this class for
 * more complex formattings.
 */
open class CstPrinter(private val outStream: OutStream): CstVisitor {

    /**
     * Prints a CST that is marked with a certain annotation. Override this
     * method for custom formats. The default implementation
     * ignores them.
     *
     * @param tree The cst that is wrapped inside the annotation
     * @param label The object that is used in the annotation parser. Useful
     * elements here can be enums that indicate that the wrapped cst
     * is a block or an infix symbol.
     * @return this for chaining
     */
    override fun visitRef(label: String, tree: ConcreteSyntaxTree) {
        tree.accept(this)
    }

    /**
     * Print a concrete char sequence. Override this method if you want to apply
     * special formatting rules based on the current state of this printer. For instance,
     * if the beginning of a line should be indented, this method should check
     * whether it is at the beginning of a line.
     */
    override fun visitToken(seq: CharSequence) {
        append(seq)
    }

    /**
     * Raw-print into the underlying outStream.
     */
    protected fun append(sequence: CharSequence) {
        outStream.append(sequence)
    }

    /**
     * Raw-print into the underlying outStream.
     */
    protected fun append(codePoint: Int) {
        outStream.append(codePoint)
    }
}
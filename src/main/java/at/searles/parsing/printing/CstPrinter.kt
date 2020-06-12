package at.searles.parsing.printing

/**
 * A printwriter for concrete syntax trees. Extend this class for
 * more complex formattings.
 */
open class CstPrinter(private val outStream: OutStream) {

    /**
     * Prints a CST that is marked with a certain annotation. Override this
     * method for custom formattings. The default implementation
     * ignores them.
     *
     * @param tree       The cst that is wrapped inside the annotation
     * @param annotation The object that is used in the annotation parser. Useful
     * elements here can be enums that indicate that the wrapped cst
     * is a block or an infix symbol.
     * @return this for chaining
     */
    open fun print(tree: ConcreteSyntaxTree, annotation: Any?): CstPrinter {
        return print(tree)
    }

    /**
     * Print a concrete char sequence. Override this method if you want to apply
     * special formatting rules based on the current state of this printer. For instance,
     * if the beginning of a line should be indented, this method should check
     * whether it is at the beginning of a line.
     */
    fun print(seq: CharSequence): CstPrinter {
        append(seq)
        return this
    }

    /**
     * Simple forward to ConcreteSyntaxTree.printTo(CstPrinter) that supports chaining.
     * Normally, keep this method as is.
     */
    fun print(tree: ConcreteSyntaxTree): CstPrinter {
        tree.printTo(this)
        return this
    }

    /**
     * Raw-print into the underlying outStream.
     */
    fun append(sequence: CharSequence): CstPrinter {
        outStream.append(sequence)
        return this
    }

    /**
     * Raw-print into the underlying outStream.
     */
    fun append(codePoint: Int): CstPrinter {
        outStream.append(codePoint)
        return this
    }

}
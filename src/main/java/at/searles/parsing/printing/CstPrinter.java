package at.searles.parsing.printing;

/**
 * A printwriter for concrete syntax trees. Extend this class for
 * more complex formattings.
 */
public class CstPrinter {

    protected final OutStream outStream;

    public CstPrinter(OutStream outStream) {
        this.outStream = outStream;
    }

    /**
     * Prints a CST that is marked with a certain annotation. Override this
     * method for custom formattings. The default implementation
     * ignores them.
     * @param tree The cst that is wrapped inside the annotation
     * @param annotation The object that is used in the annotation parser. Useful
     *                   elements here can be enums that indicate that the wrapped cst
     *                   is a block or an infix symbol.
     * @return this for chaining
     */
    public CstPrinter print(ConcreteSyntaxTree tree, Object annotation) {
        return print(tree);
    }

    /**
     * Simple forward to ConcreteSyntaxTree.print(CstPrinter) that supports chaining.
     * Keep this method as is.
     */
    public CstPrinter print(ConcreteSyntaxTree tree) {
        tree.print(this);
        return this;
    }

    /**
     * Print a concrete char sequence. Override this method if you want to apply
     * special formatting rules based on the current state of this printer. For instance,
     * if the beginning of a line should be indented, this method should check
     * whether it is at the beginning of a line.
     */
    public CstPrinter print(CharSequence seq) {
        outStream.append(seq);
        return this;
    }
}

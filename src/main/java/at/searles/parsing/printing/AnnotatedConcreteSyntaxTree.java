package at.searles.parsing.printing;

public class AnnotatedConcreteSyntaxTree<C> implements ConcreteSyntaxTree {
    private final ConcreteSyntaxTree parent;
    private final C annotation;

    public AnnotatedConcreteSyntaxTree(ConcreteSyntaxTree parent, C annotation) {
        this.parent = parent;
        this.annotation = annotation;
    }

    public String toString() {
        return parent.toString();
    }

    @Override
    public void print(CstPrinter printer) {
        printer.print(parent, annotation);
    }
}

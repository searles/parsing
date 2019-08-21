package at.searles.parsing.printing;

public class AnnotatedConcreteSyntaxTree<C> implements ConcreteSyntaxTree {
    private final C annotation;
    private final ConcreteSyntaxTree child;

    public AnnotatedConcreteSyntaxTree(C annotation, ConcreteSyntaxTree child) {
        this.child = child;
        this.annotation = annotation;
    }

    public String toString() {
        return child.toString();
    }

    @Override
    public void printTo(CstPrinter printer) {
        printer.print(child, annotation);
    }
}

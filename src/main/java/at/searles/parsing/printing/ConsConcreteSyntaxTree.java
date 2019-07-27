package at.searles.parsing.printing;

public class ConsConcreteSyntaxTree implements ConcreteSyntaxTree {
    private final ConcreteSyntaxTree left;
    private final ConcreteSyntaxTree right;

    public ConsConcreteSyntaxTree(ConcreteSyntaxTree left, ConcreteSyntaxTree right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return left.toString() + right.toString();
    }

    @Override
    public void printTo(CstPrinter printer) {
        left.printTo(printer);

        ConcreteSyntaxTree tree = right;

        while(tree instanceof ConsConcreteSyntaxTree) {
            ((ConsConcreteSyntaxTree) tree).left.printTo(printer);
            tree = ((ConsConcreteSyntaxTree) tree).right;
        }

        tree.printTo(printer);
    }
}

package at.searles.parsing.printing;

public class PartialConcreteSyntaxTree<L> {
    public final L left;
    public final ConcreteSyntaxTree right;

    public PartialConcreteSyntaxTree(L left, ConcreteSyntaxTree right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s>", left, right);
    }
}

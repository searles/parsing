package at.searles.parsing.printing;

public class PartialStringTree<L> {
    public final L left;
    public final StringTree right;

    public PartialStringTree(L left, StringTree right) {
        this.left = left;
        this.right = right;
    }
}

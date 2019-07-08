package at.searles.parsing.printing;

import java.util.function.BiFunction;

public class ConsStringTree implements StringTree {
    private final StringTree left;
    private final StringTree right;

    public ConsStringTree(StringTree left, StringTree right) {
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return left.toString() + right.toString();
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder sb, BiFunction<Object, StringTree, StringTree> markerInserts) {
        left.toStringBuilder(sb, markerInserts);

        StringTree tree = right;

        while(tree instanceof ConsStringTree) {
            ((ConsStringTree) tree).left.toStringBuilder(sb, markerInserts);
            tree = ((ConsStringTree) tree).right;
        }

        tree.toStringBuilder(sb, markerInserts);

        return sb;
    }
}

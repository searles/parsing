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
    public <C> StringBuilder toStringBuilder(StringBuilder sb, BiFunction<C, StringTree, StringTree> markerInserts) {
        // XXX Could replace one recursion by a loop...
        left.toStringBuilder(sb, markerInserts);
        right.toStringBuilder(sb, markerInserts);
        return sb;
    }
}

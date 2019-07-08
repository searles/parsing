package at.searles.parsing.printing;

import java.util.List;
import java.util.function.BiFunction;

public class ListStringTree implements StringTree {
    private final List<StringTree> list;

    public ListStringTree(List<StringTree> list) {
        this.list = list;
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder sb, BiFunction<Object, StringTree, StringTree> markerInserts) {
        for(StringTree tree : list) {
            tree.toStringBuilder(sb, markerInserts);
        }

        return sb;
    }

    public String toString() {
        return toStringBuilder(new StringBuilder(), (annotation, parent) -> parent).toString();
    }
}

package at.searles.parsing.printing;

import java.util.List;
import java.util.function.BiFunction;

public interface StringTree {
    StringTree EMPTY = new EmptyStringTree();

    default StringTree consRight(String right) {
        return consRight(new LeafStringTree(right));
    }

    default StringTree consRight(StringTree right) {
        return new ConsStringTree(this, right);
    }


    default StringTree consLeft(String left) {
        return consLeft(new LeafStringTree(left));
    }

    default StringTree consLeft(StringTree left) {
        return new ConsStringTree(left, this);
    }

    default <C> StringTree annotate(C annotate) {
        return new AnnotatedStringTree<>(this, annotate);
    }

    StringBuilder toStringBuilder(StringBuilder sb, BiFunction<Object, StringTree, StringTree> markerInserts);

    static StringTree empty() {
        return EMPTY;
    }

    static StringTree fromCharSequence(CharSequence seq) {
        return new LeafStringTree(seq);
    }

    static StringTree fromList(List<StringTree> list) {
        if(list.isEmpty()) {
            return empty();
        } else if(list.size() == 1) {
            return list.get(0);
        } else if(list.size() == 2) {
            return list.get(0).consRight(list.get(1));
        } else {
            return new ListStringTree(list);
        }
    }
}

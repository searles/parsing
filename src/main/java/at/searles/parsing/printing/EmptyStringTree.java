package at.searles.parsing.printing;

import java.util.function.BiFunction;

public class EmptyStringTree implements StringTree {
    @Override
    public StringTree consRight(StringTree right) {
        return right;
    }

    @Override
    public StringTree consLeft(StringTree left) {
        return left;
    }

    @Override
    public <C> StringBuilder toStringBuilder(StringBuilder sb, BiFunction<C, StringTree, StringTree> markerInserts) {
        return sb;
    }

    public String toString() {
        return "";
    }
}

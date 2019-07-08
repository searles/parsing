package at.searles.parsing.printing;

import java.util.function.BiFunction;

public class LeafStringTree implements StringTree {
    private final CharSequence seq;

    public LeafStringTree(CharSequence seq) {
        this.seq = seq;
    }

    public String toString() {
        return seq.toString();
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder sb, BiFunction<Object, StringTree, StringTree> markerInserts) {
        return sb.append(seq);
    }
}

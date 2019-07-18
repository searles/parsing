package at.searles.parsing.printing;

public class LeafConcreteSyntaxTree implements ConcreteSyntaxTree {
    private final CharSequence seq;

    public LeafConcreteSyntaxTree(CharSequence seq) {
        this.seq = seq;
    }

    public String toString() {
        return seq.toString();
    }

    @Override
    public void print(CstPrinter printer) {
        printer.print(seq);
    }
}

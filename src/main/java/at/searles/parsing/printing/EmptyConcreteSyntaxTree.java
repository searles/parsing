package at.searles.parsing.printing;

public class EmptyConcreteSyntaxTree implements ConcreteSyntaxTree {
    @Override
    public ConcreteSyntaxTree consRight(ConcreteSyntaxTree right) {
        return right;
    }

    @Override
    public ConcreteSyntaxTree consLeft(ConcreteSyntaxTree left) {
        return left;
    }

    @Override
    public void print(CstPrinter printer) {
    }

    public String toString() {
        return "";
    }
}

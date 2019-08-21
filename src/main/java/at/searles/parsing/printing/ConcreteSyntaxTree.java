package at.searles.parsing.printing;

import java.util.List;

public interface ConcreteSyntaxTree {

    ConcreteSyntaxTree EMPTY = new EmptyConcreteSyntaxTree();

    static ConcreteSyntaxTree empty() {
        return EMPTY;
    }

    static ConcreteSyntaxTree fromCharSequence(CharSequence seq) {
        return new LeafConcreteSyntaxTree(seq);
    }

    static ConcreteSyntaxTree fromList(List<ConcreteSyntaxTree> list) {
        if (list.isEmpty()) {
            return empty();
        } else if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() == 2) {
            return list.get(0).consRight(list.get(1));
        } else {
            return new ListConcreteSyntaxTree(list);
        }
    }

    /**
     * Print this syntax tree using the provided printer.
     *
     * @param printer The non-null printer.
     */
    void printTo(CstPrinter printer);

    default ConcreteSyntaxTree consRight(ConcreteSyntaxTree right) {
        return new ConsConcreteSyntaxTree(this, right);
    }

    default ConcreteSyntaxTree consLeft(ConcreteSyntaxTree left) {
        return new ConsConcreteSyntaxTree(left, this);
    }

    default <C> ConcreteSyntaxTree annotate(C annotate) {
        return new AnnotatedConcreteSyntaxTree<>(annotate, this);
    }
}

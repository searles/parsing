package at.searles.parsing.printing;

import java.util.List;

public class ListConcreteSyntaxTree implements ConcreteSyntaxTree {
    private final List<ConcreteSyntaxTree> list;

    public ListConcreteSyntaxTree(List<ConcreteSyntaxTree> list) {
        this.list = list;
    }

    @Override
    public void print(CstPrinter printer) {
        for(ConcreteSyntaxTree tree : list) {
            tree.print(printer);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(ConcreteSyntaxTree cst : list) {
            stringBuilder.append(cst);
        }

        return stringBuilder.toString();
    }
}

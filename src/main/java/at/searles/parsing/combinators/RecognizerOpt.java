package at.searles.parsing.combinators;

import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
public class RecognizerOpt implements Recognizer, Recognizable.Opt {

    private final Recognizer parent;
    private final boolean alwaysPrint;

    public RecognizerOpt(Recognizer parent, boolean alwaysPrint) {
        this.parent = parent;
        this.alwaysPrint = alwaysPrint;
    }

    @Override
    public Recognizable parent() {
        return parent;
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print() {
        if (alwaysPrint) {
            return parent.print();
        }

        return ConcreteSyntaxTree.empty();
    }

    @Override
    public String toString() {
        return createString();
    }
}

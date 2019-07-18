package at.searles.parsing.combinators;

import at.searles.parsing.Environment;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;

/**
 * Possibly empty repetition
 */
public class RecognizerRep implements Recognizer, Recognizable.Rep {

    private final Recognizer parent;

    public RecognizerRep(Recognizer parent) {
        this.parent = parent;
    }

    @Override
    public Recognizable parent() {
        return parent;
    }

    @Override
    public ConcreteSyntaxTree print(Environment env) {
        return ConcreteSyntaxTree.empty();
    }

    @Override
    public String toString() {
        return createString();
    }
}

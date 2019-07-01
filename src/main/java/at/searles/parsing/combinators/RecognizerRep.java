package at.searles.parsing.combinators;

import at.searles.parsing.Environment;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.StringTree;

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
    public StringTree print(Environment env) {
        return StringTree.empty();
    }

    @Override
    public String toString() {
        return createString();
    }
}

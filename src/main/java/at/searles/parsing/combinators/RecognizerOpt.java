package at.searles.parsing.combinators;

import at.searles.parsing.Environment;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.StringTree;

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
public class RecognizerOpt implements Recognizer, Recognizable.Opt {

    private final Recognizer parent;
    private boolean alwaysPrint;

    public RecognizerOpt(Recognizer parent, boolean alwaysPrint) {
        this.parent = parent;
        this.alwaysPrint = alwaysPrint;
    }

    @Override
    public Recognizable parent() {
        return parent;
    }

    @Override
    public StringTree print(Environment env) {
        if(alwaysPrint) {
            return parent.print(env);
        }

        return StringTree.empty();
    }

    @Override
    public String toString() {
        return createString();
    }
}

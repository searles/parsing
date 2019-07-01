package at.searles.parsing.combinators;

import at.searles.parsing.Environment;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.StringTree;

/**
 *
 */
public class RecognizerThenRecognizer<C extends Environment> implements Recognizer, Recognizable.Then {

    private final Recognizer left;
    private final Recognizer right;

    public RecognizerThenRecognizer(Recognizer left, Recognizer right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Recognizable left() {
        return left;
    }

    @Override
    public Recognizable right() {
        return right;
    }

    @Override
    public StringTree print(Environment env) {
        return left.print(env).consRight(right.print(env));
    }

    @Override
    public String toString() {
        return createString();
    }
}

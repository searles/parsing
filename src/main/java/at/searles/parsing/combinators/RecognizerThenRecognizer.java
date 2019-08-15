package at.searles.parsing.combinators;

import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class RecognizerThenRecognizer implements Recognizer, Recognizable.Then {

    private final Recognizer left;
    private final Recognizer right;
    private final boolean allowParserBacktrack;

    public RecognizerThenRecognizer(Recognizer left, Recognizer right, boolean allowParserBacktrack) {
        this.left = left;
        this.right = right;
        this.allowParserBacktrack = allowParserBacktrack;
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
    public boolean allowParserBacktrack() {
        return allowParserBacktrack;
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print() {
        return left.print().consRight(right.print());
    }

    @Override
    public String toString() {
        return createString();
    }
}

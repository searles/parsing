package at.searles.parsing.combinators;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.PrinterCallBack;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class RecognizerThenRecognizer<C extends ParserCallBack> implements Recognizer, Recognizable.Then {

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

    @NotNull
    @Override
    public ConcreteSyntaxTree print(PrinterCallBack env) {
        return left.print(env).consRight(right.print(env));
    }

    @Override
    public String toString() {
        return createString();
    }
}

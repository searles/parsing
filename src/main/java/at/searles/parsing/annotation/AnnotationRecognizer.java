package at.searles.parsing.annotation;

import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 * Using this parser we obtain recursion.
 */
public class AnnotationRecognizer<A> implements Recognizer {

    private final A annotate;
    private final Recognizer recognizer;

    public AnnotationRecognizer(A annotate, Recognizer recognizer) {
        this.annotate = annotate;
        this.recognizer = recognizer;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return recognizer.recognize(stream);
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print() {
        return recognizer.print().annotate(annotate);
    }

    @Override
    public String toString() {
        return recognizer.toString();
    }
}
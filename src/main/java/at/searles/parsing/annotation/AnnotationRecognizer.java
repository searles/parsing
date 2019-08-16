package at.searles.parsing.annotation;

import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 * Using this parser we obtain recursion.
 */
public class AnnotationRecognizer<A> implements Recognizer {

    private final A annotation;
    private final Recognizer recognizer;

    public AnnotationRecognizer(A annotation, Recognizer recognizer) {
        this.annotation = annotation;
        this.recognizer = recognizer;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        stream.notifyAnnotationBegin(annotation);
        boolean success = recognizer.recognize(stream);
        stream.notifyAnnotationEnd(annotation, success);
        return success;
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print() {
        return recognizer.print().annotate(annotation);
    }

    @Override
    public String toString() {
        return recognizer.toString();
    }
}
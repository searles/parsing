package at.searles.parsing.annotation;

import at.searles.parsing.Environment;
import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.StringTree;

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
    public boolean recognize(Environment env, ParserStream stream) {
        return recognizer.recognize(env, stream);
    }

    @Override
    public StringTree print(Environment env) {
        return recognizer.print(env).annotate(annotate);
    }

    @Override
    public String toString() {
        return recognizer.toString();
    }
}
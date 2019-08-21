package at.searles.parsing.annotation;

import at.searles.parsing.Parser;
import at.searles.parsing.ParserStream;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class AnnotationParser<C, T> implements Parser<T> {

    private final C annotation;
    private final Parser<T> parser;

    public AnnotationParser(C annotation, Parser<T> parser) {
        this.annotation = annotation;
        this.parser = parser;
    }

    @Override
    public T parse(ParserStream stream) {
        stream.notifyAnnotationBegin(annotation);
        T value = parser.parse(stream);
        stream.notifyAnnotationEnd(annotation, value != null);
        return value;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        stream.notifyAnnotationBegin(annotation);
        boolean success = parser.recognize(stream);
        stream.notifyAnnotationEnd(annotation, success);
        return success;
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        ConcreteSyntaxTree output = parser.print(t);

        if (output == null) {
            return null;
        }

        return output.annotate(annotation);
    }

    @Override
    public String toString() {
        return parser.toString();
    }
}
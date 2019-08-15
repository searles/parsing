package at.searles.parsing.annotation;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Parser;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class AnnotationParser<C, T> implements Parser<T> {

    private final C annotation;
    private final Parser<T> parser;

    public AnnotationParser(C annotation, Parser<T> parser) {
        this.annotation = annotation;
        this.parser = parser;
    }

    @Override
    public T parse(ParserCallBack env, ParserStream stream) {
        return parser.parse(env, stream);
    }

    @Override
    public boolean recognize(ParserCallBack env, ParserStream stream) {
        return parser.recognize(env, stream);
    }

    @Override
    public ConcreteSyntaxTree print(PrinterCallBack env, T t) {
        ConcreteSyntaxTree output = parser.print(env, t);

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
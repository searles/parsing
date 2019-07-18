package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class RecognizerToReducer<T> implements Reducer<T, T> {
    private final Recognizer recognizer;

    public RecognizerToReducer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }

    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        long preStart = stream.start();

        boolean status = recognizer.recognize(env, stream);

        if(status) {
            stream.setStart(preStart);
        }

        return status;
    }


    @Override
    public T parse(Environment env, @NotNull T left, ParserStream stream) {
        long preStart = stream.start();

        if(!recognizer.recognize(env, stream)) {
            return null;
        }

        stream.setStart(preStart);
        return left;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(Environment env, @NotNull T t) {
        return new PartialConcreteSyntaxTree<>(t, recognizer.print(env));
    }

    @Override
    public String toString() {
        return recognizer.toString() + "{x->x}";
    }
}

package at.searles.parsing.combinators;

import at.searles.parsing.Environment;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 * Multiple recognizers combined with 'or'.
 */
public class RecognizerOrRecognizer<C extends Environment> implements Recognizer, Recognizable.Or {

    private final Recognizer first;
    private final Recognizer second;

    public RecognizerOrRecognizer(Recognizer first, Recognizer second) {
        this.first = first;
        this.second = second;
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print(Environment env) {
        return first.print(env);
    }

    @Override
    public String toString() {
        return createString();
    }

    @Override
    public Recognizable first() {
        return first;
    }

    @Override
    public Recognizable second() {
        return second;
    }
}

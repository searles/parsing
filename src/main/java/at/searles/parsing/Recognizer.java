package at.searles.parsing;

import at.searles.lexer.Tokenizer;
import at.searles.parsing.combinators.*;
import at.searles.parsing.annotation.AnnotationRecognizer;
import at.searles.parsing.printing.StringTree;
import at.searles.parsing.tokens.TokenRecognizer;
import org.jetbrains.annotations.NotNull;

public interface Recognizer extends Recognizable {

    /**
     * Must not return null!
     */
    @NotNull StringTree print(Environment env);

    default <T> Reducer<T, T> toReducer() {
        // corresponds to prefix.
        return new RecognizerToReducer<>(this);
    }

    default Recognizer then(Recognizer recognizer) {
        return new RecognizerThenRecognizer<>(this, recognizer);
    }

    default <T> Parser<T> then(Parser<T> parser) {
        // corresponds to prefix.
        return new RecognizerThenParser<>(this, parser);
    }

    default <T, U> Reducer<T, U> then(Reducer<T, U> parser) {
        // corresponds to prefix.
        return new RecognizerThenReducer<>(this, parser);
    }

    default Recognizer or(Recognizer recognizer) {
        return new RecognizerOrRecognizer<>(this, recognizer);
    }

    default Recognizer rep() { //Caution in printer!
        return new RecognizerRep(this);
    }

    default Recognizer plus() { //Caution in printer!
        return this.then(rep());
    }

    default Recognizer opt() {
        return opt(false);
    }

    default Recognizer opt(boolean alwaysPrint) {
        return new RecognizerOpt(this, alwaysPrint);
    }

    /**
     * Creates a reducer for a possibly empty csv-alike structure
     */
    default <T> Reducer<T, T> join(Reducer<T, T> reducer) {
        return new Join(this, reducer);
    }

    default <A> Recognizer annotate(A category) {
        return new AnnotationRecognizer<>(category, this);
    }

    static Recognizer fromString(String string, Tokenizer tokenizer, boolean exclusive) {
        return new TokenRecognizer(string, tokenizer, exclusive);
    }
}
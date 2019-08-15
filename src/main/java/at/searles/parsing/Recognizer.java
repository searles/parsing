package at.searles.parsing;

import at.searles.lexer.Tokenizer;
import at.searles.parsing.annotation.AnnotationRecognizer;
import at.searles.parsing.combinators.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.tokens.TokenRecognizer;
import at.searles.regex.CharSet;
import org.jetbrains.annotations.NotNull;

public interface Recognizer extends Recognizable {

    static Recognizer fromString(String string, Tokenizer tokenizer, boolean exclusive) {
        return new TokenRecognizer(string, tokenizer, exclusive);
    }

    /**
     * Creates a recognizer that will detect EOF (which is -1 returned from
     * the stream).
     */
    static Recognizer eof(Tokenizer tokenizer) {
        return new TokenRecognizer(tokenizer.token(CharSet.chars(-1)), false);
    }

    @NotNull
    ConcreteSyntaxTree print();

    default Recognizer then(Recognizer recognizer) {
        return then(recognizer, false);
    }

    default Recognizer then(Recognizer recognizer, boolean allowParserBacktrack) {
        return new RecognizerThenRecognizer(this, recognizer, allowParserBacktrack);
    }

    default <T> Parser<T> then(Parser<T> parser) {
        return then(parser, false);
    }

    default <T> Parser<T> then(Parser<T> parser, boolean allowParserBacktrack) {
        // corresponds to prefix.
        return new RecognizerThenParser<>(this, parser, allowParserBacktrack);
    }

    default <T, U> Reducer<T, U> then(Reducer<T, U> parser) {
        return then(parser, false);
    }

    // corresponds to prefix.
    default <T, U> Reducer<T, U> then(Reducer<T, U> parser, boolean allowParserBacktrack) {
        return new RecognizerThenReducer<>(this, parser, allowParserBacktrack);
    }

    default Recognizer or(Recognizer recognizer) {
        return new RecognizerOrRecognizer(this, recognizer);
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
        return new ReducerJoin<>(this, reducer);
    }

    default <T> Reducer<T, T> joinPlus(Reducer<T, T> reducer) {
        return new ReducerJoinPlus<>(this, reducer);
    }

    default <A> Recognizer annotate(A category) {
        return new AnnotationRecognizer<>(category, this);
    }
}
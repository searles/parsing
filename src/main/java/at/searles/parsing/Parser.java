package at.searles.parsing;

import at.searles.lexer.Token;
import at.searles.parsing.annotation.AnnotationParser;
import at.searles.parsing.combinators.ParserOrParser;
import at.searles.parsing.combinators.ParserThenRecognizer;
import at.searles.parsing.combinators.ParserThenReducer;
import at.searles.parsing.combinators.ParserToReducer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.tokens.TokenParser;
import org.jetbrains.annotations.Nullable;

/**
 * A parser is an object that reads tokens from a parser and
 * returns an object. Additionally it uses a env object
 * for feedback on errors.
 * <p>
 * Parsers are inversible. A syntax tree (a tree that is easily converted
 * into a string) is returned by the inverse. For a proper inversion,
 * a set is maintained to avoid cycles.
 *
 * @param <T>
 */
public interface Parser<T> extends Recognizable {

    /**
     * Token: lexer::regex^? ~ mapping
     */
    static <T> Parser<T> fromToken(Token token, Mapping<CharSequence, T> mapping, boolean exclusive) {
        return new TokenParser<>(token, mapping, exclusive);
    }

    /**
     * Returns the parsed value.
     *
     * @param env    The environment.
     * @param stream The parser stream from which items are read.
     * @return An instance of T or null if this parser cannot be used.
     */
    @Nullable
    T parse(Environment env, ParserStream stream);

    default @Nullable
    ConcreteSyntaxTree print(Environment env, T t) {
        throw new UnsupportedOperationException("printing not supported");
    }

    /**
     * A B
     */
    default <U> Parser<U> then(Reducer<T, U> reducer) {
        return new ParserThenReducer<>(this, reducer);
    }

    default Parser<T> then(Recognizer recognizer) {
        return new ParserThenRecognizer<>(this, recognizer);
    }

    /**
     * A | B
     */
    default Parser<T> or(Parser<T> alternative) {
        return or(alternative, false);
    }

    /**
     * scala: A.|(B, true)
     */
    default Parser<T> or(Parser<T> alternative, boolean swapOnInvert) {
        return new ParserOrParser<>(this, alternative, swapOnInvert);
    }

    /*
     * A.+(fold); short for
     */
//    default Parser<T> plus(Fold<T, T, T> fold) {
//        return this.then(Reducer.rep(this.fold(fold)));
//    }

    /**
     * A > fold
     */
    default <L, V> Reducer<L, V> fold(Fold<L, T, V> fold) {
        return new ParserToReducer<>(this, fold);
    }

    /**
     * Useful to simplify parser's toString output. Automtically
     * created for each rule.
     */
    default Parser<T> ref(String label) {
        return new Ref<T>(label).set(this);
    }

    /**
     * A @ annotation
     * \
     */
    default <M> Parser<T> annotate(M annotation) {
        return new AnnotationParser<>(annotation, this);
    }
}


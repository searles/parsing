package at.searles.parsing;

import at.searles.lexer.Tokenizer;
import at.searles.parsing.annotation.AnnotationParser;
import at.searles.parsing.combinators.ParserOrParser;
import at.searles.parsing.combinators.ParserThenRecognizer;
import at.searles.parsing.combinators.ParserThenReducer;
import at.searles.parsing.combinators.ParserToReducer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.tokens.TokenParser;
import at.searles.regex.Regex;
import org.jetbrains.annotations.Nullable;

/**
 * A parser is an object that reads tokens from a parser and
 * returns an object. Additionally it uses a env object
 * for feedback on errors.
 * <p>
 * Parsers are invertible. A syntax tree (a tree that is easily converted
 * into a string) is returned by the inverse. For a proper inversion,
 * a set is maintained to avoid cycles.
 *
 * @param <T>
 */
public interface Parser<T> extends Recognizable {

    /**
     * Token: lexer::regex^? ~ mapping. In fact, the value is always
     * an instance of FrameStream.Frame (this knowledge can be used
     * to obtain the position of the token).
     */
    static <T> Parser<T> fromToken(int tokenId, Tokenizer tokenizer, boolean exclusive, Mapping<CharSequence, T> mapping) {
        return new TokenParser<>(tokenId, tokenizer, exclusive, mapping);
    }

    /**
     * Token: lexer::regex^? ~ mapping. In fact, the value is always
     * an instance of FrameStream.Frame (this knowledge can be used
     * to obtain the position of the token).
     */
    static <T> Parser<T> fromRegex(Regex regex, Tokenizer tokenizer, boolean exclusive, Mapping<CharSequence, T> mapping) {
        int tokenId = tokenizer.add(regex);
        return fromToken(tokenId, tokenizer, exclusive, mapping);
    }

    /**
     * Returns the parsed value.
     *
     * @param stream The parser stream from which items are read.
     * @return An instance of T or null if this parser cannot be used.
     */
    @Nullable
    T parse(ParserStream stream);

    default @Nullable
    ConcreteSyntaxTree print(T t) {
        throw new UnsupportedOperationException("printing not supported");
    }

    /**
     * A B
     */
    default <U> Parser<U> then(Reducer<T, U> reducer) {
        return then(reducer, false);
    }

    default <U> Parser<U> then(Reducer<T, U> reducer, boolean allowParserBacktrack) {
        return then(reducer, allowParserBacktrack, false);
    }

    default <U> Parser<U> then(Reducer<T, U> reducer, boolean allowParserBacktrack, boolean allowPrinterBacktrack) {
        return new ParserThenReducer<>(this, reducer, allowParserBacktrack, allowPrinterBacktrack);
    }

    default Parser<T> then(Recognizer recognizer) {
        return then(recognizer, false);
    }

    default Parser<T> then(Recognizer recognizer, boolean allowParserBacktrack) {
        return new ParserThenRecognizer<>(this, recognizer, allowParserBacktrack);
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

    /**
     * A > fold
     */
    default <L, V> Reducer<L, V> fold(Fold<L, T, V> fold) {
        return new ParserToReducer<>(this, fold);
    }

    /**
     * Useful to simplify parser's toString output. Automatically
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


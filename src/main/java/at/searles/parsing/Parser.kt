package at.searles.parsing

import at.searles.lexer.Tokenizer
import at.searles.parsing.annotation.AnnotationParser
import at.searles.parsing.combinators.*
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.tokens.TokenParser
import at.searles.regex.Regex

/**
 * A parser is an object that reads tokens from a parser and
 * returns an object. Additionally it uses a env object
 * for feedback on errors.
 *
 *
 * Parsers are invertible. A syntax tree (a tree that is easily converted
 * into a string) is returned by the inverse. For a proper inversion,
 * a set is maintained to avoid cycles.
 *
 * @param <T>
</T> */
interface Parser<T> : Recognizable {
    /**
     * Returns the parsed value.
     *
     * @param stream The parser stream from which items are read.
     * @return An instance of T or null if this parser cannot be used.
     */
    fun parse(stream: ParserStream): T?

    fun print(item: T): ConcreteSyntaxTree? {
        throw UnsupportedOperationException("printing not supported")
    }

    operator fun <U> plus(right: Reducer<T, U>): Parser<U> {
        return ParserThenReducer(this, right)
    }

    operator fun plus(right: Recognizer): Parser<T> {
        return ParserThenRecognizer(this, right)
    }

    /**
     * A | B
     */
    infix fun or(alternative: Parser<T>): Parser<T> {
        return ParserOrParser(this, alternative)
    }

    /**
     * A | B
     */
    infix fun orSwapOnPrint(alternative: Parser<T>): Parser<T> {
        return ParserOrParserWithReversedPrintOrder(this, alternative)
    }

    /**
     * A > fold
     */
    fun <L, V> fold(fold: Fold<L, T, V>): Reducer<L, V> {
        return ParserToReducer(this, fold)
    }

    /**
     * Useful to simplify parser's toString output. Automatically
     * created for each rule.
     */
    fun ref(label: String): Parser<T> {
        return Ref<T>(label).apply {
            ref = this@Parser
        }
    }

    /**
     * A @ annotation
     * \
     */
    fun <M> annotate(annotation: M): Parser<T> {
        return AnnotationParser(annotation, this)
    }

    companion object {
        /**
         * Token: lexer::regex^? ~ mapping. In fact, the value is always
         * an instance of FrameStream.Frame (this knowledge can be used
         * to obtain the position of the token).
         */
        fun <T> fromToken(tokenId: Int, tokenizer: Tokenizer, exclusive: Boolean, mapping: Mapping<CharSequence, T>): Parser<T> {
            return TokenParser(tokenId, tokenizer, exclusive) + mapping
        }

        /**
         * Token: lexer::regex^? ~ mapping. In fact, the value is always
         * an instance of FrameStream.Frame (this knowledge can be used
         * to obtain the position of the token).
         */
        fun <T> fromRegex(regex: Regex, tokenizer: Tokenizer, exclusive: Boolean, mapping: Mapping<CharSequence, T>): Parser<T> {
            val tokenId = tokenizer.add(regex)
            return fromToken(tokenId, tokenizer, exclusive, mapping)
        }
    }
}
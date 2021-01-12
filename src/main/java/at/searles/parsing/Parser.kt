package at.searles.parsing

import at.searles.lexer.Tokenizer
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.combinators.*
import at.searles.parsing.combinators.ext.ParserOrParserReversePrintOrder
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.ref.RefParser
import at.searles.parsing.tokens.TokenParser
import at.searles.parsingtools.common.PairCreator
import at.searles.parsingtools.list.EmptyListCreator
import at.searles.parsingtools.list.ListAppender
import at.searles.parsingtools.list.ListCreator
import at.searles.parsingtools.opt.NoneCreator
import at.searles.parsingtools.opt.SomeCreator
import at.searles.regexp.Regexp
import java.util.*

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
interface Parser<T>: CanRecognize {
    /**
     * Returns the parsed value.
     *
     * Contract: If the parser is successful, stream's interval is set to the position of the
     * successful parse.
     *
     * @param stream The parser stream from which items are read.
     * @return An instance of T or null if this parser cannot be used.
     */
    fun parse(stream: ParserStream): T?

    override fun recognize(stream: ParserStream): Boolean

    fun print(item: T): ConcreteSyntaxTree? {
        throw UnsupportedOperationException("printing not supported")
    }

    fun rep1(separator: Recognizer): Parser<List<T>> {
        return (this + ListCreator()) + (separator + this.plus(ListAppender(1))).rep()
    }

    fun rep1(): Parser<List<T>> {
        return (this + ListCreator()) + this.plus(ListAppender(1)).rep()
    }

    fun rep(separator: Recognizer): Parser<List<T>> {
        return EmptyListCreator<T>() + separator.join(this.plus(ListAppender(0)))
    }

    fun rep(): Parser<List<T>> {
        return EmptyListCreator<T>() + this.plus(ListAppender(0)).rep()
    }

    fun opt(): Parser<Optional<T>> {
        return this + SomeCreator() or NoneCreator()
    }

    infix fun or(other: Parser<T>): Parser<T> {
        return ParserOrParser(this, other)
    }

    infix fun orSwapOnPrint(other: Parser<T>): Parser<T> {
        return ParserOrParserReversePrintOrder(this, other)
    }

    operator fun <L, V> plus(fold: Fold<L, T, V>): Reducer<L, V> {
        return ParserToReducer(this, fold)
    }

    operator fun <U> plus(right: Reducer<T, U>): Parser<U> {
        return ParserThenReducer(this, right)
    }

    operator fun plus(right: Recognizer): Parser<T> {
        return this + right.toReducer()
    }

    operator fun <U> plus(right: Parser<U>): Parser<Pair<T, U>> {
        return this + right.plus(PairCreator())
    }

    /**
     * Useful to simplify parser's toString output. Automatically
     * created for each rule.
     */
    fun ref(label: String): Parser<T> {
        return RefParser<T>(label).apply {
            ref = this@Parser
        }
    }

    fun <L> toPair(): Reducer<L, Pair<L, T>> {
        return this + PairCreator()
    }

    companion object {
        /**
         * Token: lexer::regex^? ~ mapping. In fact, the value is always
         * an instance of FrameStream.Frame (this knowledge can be used
         * to obtain the position of the token).
         */
        fun <T> fromToken(tokenId: Int, tokenizer: Tokenizer, mapping: Mapping<CharSequence, T>): Parser<T> {
            return TokenParser(tokenId, tokenizer) + mapping
        }

        /**
         * Token: lexer::regex^? ~ mapping. In fact, the value is always
         * an instance of FrameStream.Frame (this knowledge can be used
         * to obtain the position of the token).
         */
        fun <T> fromRegex(regexp: Regexp, tokenizer: Tokenizer, mapping: Mapping<CharSequence, T>): Parser<T> {
            val tokenId = tokenizer.add(regexp)
            return fromToken(tokenId, tokenizer, mapping)
        }

        fun <T> Parser<List<T>>.orEmpty(): Parser<List<T>> {
            return this orSwapOnPrint EmptyListCreator()
        }
    }
}
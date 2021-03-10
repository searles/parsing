package at.searles.parsing.parser

import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.Reducer.Companion.join
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.parser.tools.*
import at.searles.parsing.printer.PrintTree

interface Parser<A> {
    fun parse(string: String): ParserResult<A> {
        // for convenience
        val stream = ParserStream(string)
        return parse(stream)
    }

    fun parse(stream: ParserStream): ParserResult<A>
    fun print(value: A): PrintTree

    infix fun or(other: Parser<A>): Parser<A> {
        return ParserUnion(listOf(this, other))
    }

    fun or(other: Parser<A>, swapPrint: Boolean): Parser<A> {
        return if(swapPrint) ParserPrinterSeparate(this or other, other or this) else this or other
    }

    operator fun plus(recognizer: Recognizer): Parser<A> {
        return this + recognizer.toReducer()
    }

    operator fun <B> plus(parser: Parser<B>): Parser<Pair<A, B>> {
        return this + (parser + CreatePair())
    }

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return ParserPlusReducer(this, reducer)
    }

    operator fun <B, C> plus(fold: Fold<B, A, C>): Reducer<B, C> {
        return ParserPlusFold(this, fold)
    }

    fun rep(minCount: Int = 0): Parser<List<A>> {
        return CreateEmptyList<A>() + (this + ListAppend()).rep(minCount)
    }

    fun opt(): Parser<A?> {
        return this + CastToNullable() or InitValue(null)
    }

    fun join(separator: Recognizer): Parser<List<A>> {
        return CreateEmptyList<A>() + (this + ListAppend()).join(separator)
    }

    fun select(highlight: Any): Parser<A> {
        return SelectParser(highlight, this)
    }

    companion object {
        fun <A> Parser<List<A>>.orEmpty(): Parser<List<A>> {
            return ParserPrinterSeparate(this or CreateEmptyList(), CreateEmptyList<A>() or this)
        }

        fun <A> variation(vararg others: Parser<A>): Parser<List<A>> {
            return CreateEmptyList<A>() + Variation(others.map { it + ListAppend() }.toList())
        }

        fun Parser<CharSequence>.asString(): Parser<String> {
            return this + AsString
        }
    }
}
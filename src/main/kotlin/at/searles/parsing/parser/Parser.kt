package at.searles.parsing.parser

import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.Reducer.Companion.join
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.parser.tools.CastToNullable
import at.searles.parsing.parser.tools.CreatePair
import at.searles.parsing.parser.tools.EmptyList
import at.searles.parsing.parser.tools.ListAppend
import at.searles.parsing.printer.PrintTree

interface Parser<A> {
    fun parse(stream: ParserStream): ParserResult<A>
    fun print(value: A): PrintTree

    infix fun or(other: Parser<A>): Parser<A> {
        return ParserUnion(listOf(this, other))
    }

    fun or(other: Parser<A>, swapPrint: Boolean): Parser<A> {
        return if(swapPrint) ParserUnion(listOf(other, this)) else this or other
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
        return EmptyList<A>() + (this + ListAppend()).rep(minCount)
    }

    fun opt(): Parser<A?> {
        return this + CastToNullable() or InitValue { null }
    }

    fun join(separator: Recognizer): Parser<List<A>> {
        return EmptyList<A>() + (this + ListAppend()).join(separator)
    }

    companion object {
        fun <A> Parser<List<A>>.orEmpty(): Parser<List<A>> {
            return ParserPrinterSeparate(this or EmptyList(), EmptyList<A>() or this)
        }
    }
}
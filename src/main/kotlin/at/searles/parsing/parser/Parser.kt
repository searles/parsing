package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.ParserUnion
import at.searles.parsing.parser.combinators.ParserPlusFold
import at.searles.parsing.parser.combinators.ParserPlusReducer
import at.searles.parsing.printer.PrintTree

interface Parser<A> {
    fun parse(stream: ParserStream): ParserResult<A>
    fun print(value: A): PrintTree

    operator fun plus(recognizer: Recognizer): Parser<A> {
        return this + recognizer.toReducer()
    }

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return ParserPlusReducer(this, reducer)
    }

    operator fun <B, C> plus(fold: Fold<B, A, C>): Reducer<B, C> {
        return ParserPlusFold(this, fold)
    }

    infix fun or(other: Parser<A>): Parser<A> {
        return ParserUnion<A>(listOf(this, other))
    }
}
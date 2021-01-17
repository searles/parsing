package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.ParserPlusFold
import at.searles.parsing.parser.combinators.ParserPlusReducer

interface Parser<A> {
    fun parse(stream: ParserStream): ParserResult<A>

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return ParserPlusReducer(this, reducer)
    }

    operator fun <B, C> plus(fold: Fold<B, A, C>): Reducer<B, C> {
        return ParserPlusFold(this, fold)
    }
}
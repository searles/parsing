package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.OptionalReducer
import at.searles.parsing.parser.combinators.RepeatReducer
import at.searles.parsing.printer.PartialPrintTree

interface Reducer<A, B> {
    fun parse(stream: ParserStream, input: A): ParserResult<B>
    fun print(value: B): PartialPrintTree<A>

    companion object {
        fun <A> Reducer<A, A>.rep(minCount: Int = 0): Reducer<A, A> {
            return RepeatReducer(this, minCount)
        }

        fun <A> Reducer<A, A>.opt(): Reducer<A, A> {
            return OptionalReducer(this)
        }
    }
}
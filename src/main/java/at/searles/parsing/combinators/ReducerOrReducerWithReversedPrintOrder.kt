package at.searles.parsing.combinators

import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

open class ReducerOrReducerWithReversedPrintOrder<T, U>(override val choice0: Reducer<T, U>, override val choice1: Reducer<T, U>) : ReducerOrReducer<T, U>(choice0, choice1) {
    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        return choice1.print(item) ?: choice0.print(item)
    }
}
package at.searles.parsing.combinators.ext

import at.searles.parsing.Reducer
import at.searles.parsing.combinators.ReducerOrReducer
import at.searles.parsing.printing.PartialTree

open class ReducerOrReducerWithReversedPrintOrder<T, U>(vararg choices: Reducer<T, U>) : ReducerOrReducer<T, U>(*choices) {
    private val reversedChoices by lazy { choices.reversed() }

    override fun print(item: U): PartialTree<T>? {
        for(choice in reversedChoices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }
}
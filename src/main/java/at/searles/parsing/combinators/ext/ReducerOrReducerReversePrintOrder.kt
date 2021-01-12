package at.searles.parsing.combinators.ext

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.combinators.ReducerOrReducer
import at.searles.parsing.printing.PartialTree

class ReducerOrReducerReversePrintOrder<T, U>(vararg val choices: Reducer<T, U>) : Reducer<T, U> {
    private val reversedChoices by lazy { choices.reversed() }

    override fun print(item: U): PartialTree<T>? {
        for(choice in reversedChoices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }

    override fun reduce(left: T, stream: ParserStream): U? {
        for(choice in choices) {
            stream.reduce(left, choice)?.let {
                return it
            }
        }

        return null
    }

    override fun recognize(stream: ParserStream): Boolean {
        for(choice in choices) {
            if(stream.recognize(choice, false)) {
                return true
            }
        }

        return false
    }

    override fun toString(): String {
        return "${choices.first()}.or(${choices.drop(1).joinToString(", ")})"
    }

}
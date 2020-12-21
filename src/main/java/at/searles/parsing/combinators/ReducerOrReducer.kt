package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

open class ReducerOrReducer<T, U>(private vararg val choices: Reducer<T, U>) : Reducer<T,U> {
    override fun or(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerOrReducer(*choices, other)
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

    override fun print(item: U): PartialTree<T>? {
        for(choice in choices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }

    override fun toString(): String {
        return "${choices.first()}.or(${choices.drop(1).joinToString(", ")})"
    }
}
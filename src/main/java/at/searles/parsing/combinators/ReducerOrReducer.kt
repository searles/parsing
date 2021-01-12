package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

class ReducerOrReducer<T, U>(private val parseOrder: List<Reducer<T, U>>, private val printOrder: List<Reducer<T, U>>) : Reducer<T,U> {
    override fun or(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerOrReducer(parseOrder + other, printOrder + other)
    }

    override fun orSwapOnPrint(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerOrReducer(parseOrder + other, listOf(other) + printOrder)
    }

    override fun reduce(left: T, stream: ParserStream): U? {
        for(choice in parseOrder) {
            stream.reduce(left, choice)?.let {
                return it
            }
        }

        return null
    }

    override fun recognize(stream: ParserStream): Boolean {
        for(choice in parseOrder) {
            if(stream.recognize(choice, false)) {
                return true
            }
        }

        return false
    }

    override fun print(item: U): PartialTree<T>? {
        for(choice in printOrder) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }

    override fun <V> plus(right: Reducer<U, V>): Reducer<T, V> {
        return ReducerOrReducer(
            parseOrder.map { it + right },
            printOrder.map { it + right }
        )
    }

    override fun plus(right: Recognizer): Reducer<T, U> {
        return ReducerOrReducer(
            parseOrder.map { it + right },
            printOrder.map { it + right }
        )
    }

    override fun toString(): String {
        return "${parseOrder.first()}.or(${parseOrder.drop(1).joinToString(", ")})"
    }
}
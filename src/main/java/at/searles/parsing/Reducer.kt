package at.searles.parsing

import at.searles.parsing.combinators.*
import at.searles.parsing.combinators.ext.ReducerOrReducerWithReversedPrintOrder
import at.searles.parsing.combinators.ext.ReducerPlus
import at.searles.parsing.printing.PartialTree
import at.searles.parsing.ref.RefReducer

interface Reducer<T, U>: CanRecognize {
    /**
     * Parses elements from TokStream
     *
     * @param stream The stream from which elements are read
     * @param left   The element left of this reducer
     * @return The parsed element, null if parsing was not successful.
     */
    fun reduce(left: T, stream: ParserStream): U?

    override fun recognize(stream: ParserStream): Boolean

    /**
     * Prints the argument that is split of u on its right. It is the
     * counterpart of the left method. This method always succeeds if left succeeds.
     * Otherwise, it will trigger an error via env.
     *
     * @param item   The argument
     * @return null if fail
     */
    fun print(item: U): PartialTree<T>? {
        throw UnsupportedOperationException("printing not supported")
    }

    operator fun <V> plus(right: Reducer<U, V>): Reducer<T, V> {
        return ReducerThenReducer(this, right)
    }

    operator fun plus(right: Recognizer): Reducer<T, U> {
        return this + right.toReducer()
    }

    infix fun or(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerOrReducer(this, other)
    }

    infix fun orSwapOnPrint(other: Reducer<T, U>): Reducer<T, U> {
        return ReducerOrReducerWithReversedPrintOrder(this, other)
    }

    fun ref(label: String): Reducer<T, U> {
        return RefReducer<T, U>(label).apply {
            ref = this@Reducer
        }
    }

    companion object {
        fun <T> Reducer<T, T>.opt(): Reducer<T, T> {
            return ReducerOpt(this)
        }

        fun <T> Reducer<T, T>.rep(): Reducer<T, T> {
            return ReducerRep(this)
        }

        fun <T> Reducer<T, T>.rep1(): Reducer<T, T> {
            return ReducerPlus(this, 1)
        }

        fun <T> Reducer<T, T>.rep(min: Int): Reducer<T, T> {
            return ReducerPlus(this, min)
        }

        infix fun <T> Reducer<T, T>.or(other: Recognizer): Reducer<T, T> {
            return this or other + Mapping.create { it }
        }
    }
}
package at.searles.parsing.ref

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

class RefReducer<T, U>(private val label: String) : Reducer<T, U> {

    var ref: Reducer<T, U>
        get() = internalRef ?: error("$label is not initialized")

        set(value) {
            internalRef = value
        }

    private var internalRef: Reducer<T, U>? = null

    override fun parse(stream: ParserStream, input: T): U? {
        return ref.parse(stream, input)
    }

    override fun print(item: U): PartialTree<T>? {
        return ref.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return ref.recognize(stream)
    }

    override fun toString(): String {
        return label
    }

}
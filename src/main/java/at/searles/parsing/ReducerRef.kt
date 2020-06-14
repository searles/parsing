package at.searles.parsing

import at.searles.parsing.printing.PartialConcreteSyntaxTree

class ReducerRef<T, U>(private val label: String) : Reducer<T, U> {

    lateinit var ref: Reducer<T, U>

    override fun parse(stream: ParserStream, input: T): U? {
        return ref.parse(stream, input)
    }

    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        return ref.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return ref.recognize(stream)
    }

    override fun toString(): String {
        return label
    }

}
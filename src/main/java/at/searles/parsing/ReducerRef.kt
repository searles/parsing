package at.searles.parsing

import at.searles.parsing.printing.PartialConcreteSyntaxTree

class ReducerRef<T, U>(private val parent: Reducer<T, U>, private val label: String) : Reducer<T, U> {
    override fun parse(stream: ParserStream, input: T): U? {
        return parent.parse(stream, input)
    }

    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        return parent.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return parent.recognize(stream)
    }

    override fun toString(): String {
        return label
    }

}
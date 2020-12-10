package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree
import at.searles.parsing.printing.PartialTree

class ReducerOpt<T>(private val parent: Reducer<T, T>) : Reducer<T, T>/*, Opt*/ {

    override fun parse(left: T, stream: ParserStream): T? {
        return stream.reduce(left, parent) ?: left
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.recognize(parent)
        return true
    }

    override fun print(item: T): PartialTree<T> {
        val output = parent.print(item)
        return output ?: PartialTree(item, EmptyTree)
    }

    override fun toString(): String {
        return "$parent.opt"
    }
}
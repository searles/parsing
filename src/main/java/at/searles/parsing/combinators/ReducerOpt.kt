package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Opt
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.PartialTree

class ReducerOpt<T>(override val parent: Reducer<T, T>) : Reducer<T, T>, Opt {

    override fun parse(stream: ParserStream, input: T): T? {
        val preStart = stream.start
        val nonOptResult = parent.parse(stream, input)
        assert(stream.start == preStart)
        return nonOptResult ?: input
    }

    override fun print(item: T): PartialTree<T>? {
        val output = parent.print(item)
        return output ?: PartialTree(item, ConcreteSyntaxTree.empty())
    }

    override fun toString(): String {
        return createString()
    }

}
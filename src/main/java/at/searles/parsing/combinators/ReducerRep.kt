package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Rep
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.PartialConcreteSyntaxTree
import java.util.*

/**
 * Parser for repetitions
 */
class ReducerRep<T>(override val parent: Reducer<T, T>) : Reducer<T, T>, Rep {

    override fun parse(stream: ParserStream, input: T): T {

        var item = input
        val preStart = stream.start

        while (true) {
            val nextItem = parent.parse(stream, item)
            assert(stream.start == preStart)
            if (nextItem == null) return item
            item = nextItem
        }
    }

    override fun print(item: T): PartialConcreteSyntaxTree<T>? {
        var left: T = item
        val trees = ArrayList<ConcreteSyntaxTree>()

        while (true) {
            val next = parent.print(left) ?: break
            trees.add(next.right)
            left = next.left
        }

        trees.reverse()

        return PartialConcreteSyntaxTree(left, ConcreteSyntaxTree.fromList(trees))
    }

    override fun toString(): String {
        return createString()
    }
}
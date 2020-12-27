package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.PartialTree
import java.util.*

/**
 * Parser for repetitions
 */
class ReducerLazyRep<T>(private val parent: Reducer<T, T>) : Reducer<T, T>/*, Rep*/ {

    override fun reduce(left: T, stream: ParserStream): T? {
        var item = left

        while (true) {
            val nextItem = stream.reduce(item, parent) ?: return item
            item = nextItem
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        while (stream.recognize(parent, false)) {
            /* empty */
        }

        return true
    }

    override fun print(item: T): PartialTree<T> {
        var left: T = item
        val trees = ArrayList<ConcreteSyntaxTree>()

        while (true) {
            val next = parent.print(left) ?: break
            trees.add(next.right)
            left = next.left
        }

        trees.reverse()

        return PartialTree(left, ConcreteSyntaxTree.fromList(trees))
    }

    override fun toString(): String {
        return "$parent.rep"
    }
}
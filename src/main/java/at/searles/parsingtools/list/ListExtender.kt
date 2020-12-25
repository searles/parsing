package at.searles.parsingtools.list

import at.searles.parsing.Fold
import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

class ListExtender<A, C>(private val mapping: Mapping<A, C>): Fold<List<C>, List<A>, List<C>> {
    override fun apply(stream: ParserStream, left: List<C>, right: List<A>): List<C> {
        return right.fold(BacktrackingList.create(left)) { leftList, it ->
            leftList.pushBack(mapping.reduce(it, stream))
        }
    }

    override fun leftInverse(result: List<C>): List<C>? {
        var isApplicable = false
        val leftList = result.dropLastWhile { (mapping.left(it)?.also { isApplicable = true } != null) }

        return if(isApplicable) leftList else null
    }

    override fun rightInverse(result: List<C>): List<A>? {
        val rightList = ArrayList<A>()

        result.takeLastWhile { element ->
            mapping.left(element)?.also { rightList.add(it) } != null
        }

        if(rightList.isEmpty()) {
            return null
        }

        rightList.reverse()

        return rightList
    }

    override fun toString(): String {
        return "{[x], [y] -> [x, $mapping(y)]}"
    }
}
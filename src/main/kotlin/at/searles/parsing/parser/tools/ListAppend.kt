package at.searles.parsing.parser.tools

import at.searles.parsing.parser.FnResult
import at.searles.parsing.parser.Fold

class ListAppend<A>(private val minSize: Int = 0): Fold<List<A>, A, List<A>> {
    override fun fold(left: List<A>, right: A): List<A> {
        return BacktrackingList.create(left) + right
    }

    override fun invertLeft(value: List<A>): FnResult<List<A>> {
        if(value.size <= minSize) return FnResult.failure
        return FnResult.success(value.dropLast(1))
    }

    override fun invertRight(value: List<A>): FnResult<A> {
        if(value.size <= minSize) return FnResult.failure
        return FnResult.success(value.last())
    }
}
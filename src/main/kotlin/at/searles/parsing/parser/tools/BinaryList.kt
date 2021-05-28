package at.searles.parsing.parser.tools

import at.searles.parsing.parser.FnResult
import at.searles.parsing.parser.Fold

class BinaryList<A>: Fold<A, A, List<A>> {
    override fun fold(left: A, right: A): List<A> {
        return listOf(left, right)
    }

    override fun invertLeft(value: List<A>): FnResult<A> {
        return if(value.size != 2) FnResult.failure else FnResult.success(value.first())
    }

    override fun invertRight(value: List<A>): FnResult<A> {
        return if(value.size != 2) FnResult.failure else FnResult.success(value.last())
    }

    override fun toString(): String {
        return "{list2}"
    }
}
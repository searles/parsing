package at.searles.parsing.parser.tools

import at.searles.parsing.parser.FnResult
import at.searles.parsing.parser.Fold

class CreatePair<A, B> : Fold<A, B, Pair<A, B>> {
    override fun fold(left: A, right: B): Pair<A, B> {
        return Pair(left, right)
    }

    override fun invertLeft(value: Pair<A, B>): FnResult<A> {
        return FnResult.success(value.first)
    }

    override fun invertRight(value: Pair<A, B>): FnResult<B> {
        return FnResult.success(value.second)
    }
}

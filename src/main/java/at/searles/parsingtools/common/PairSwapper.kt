package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream
import at.searles.utils.Pair

class PairSwapper<T, U> : Fold<T, U, Pair<U, T>> {
    override fun apply(stream: ParserStream, left: T, right: U): Pair<U, T> {
        return Pair(right, left)
    }

    override fun leftInverse(result: Pair<U, T>): T? {
        return result.r()
    }

    override fun rightInverse(result: Pair<U, T>): U? {
        return result.l()
    }

    override fun toString(): String {
        return "{<y,x>}"
    }
}

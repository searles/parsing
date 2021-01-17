package at.searles.parsing.parser

interface Fold<A, B, C> {
    fun fold(left: A, right: B): C
    fun invertLeft(value: C): FnResult<A> = FnResult.failure()
    fun invertRight(value: C): FnResult<B> = FnResult.failure()
}
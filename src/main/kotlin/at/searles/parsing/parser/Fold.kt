package at.searles.parsing.parser

interface Fold<A, B, C> {
    fun fold(left: A, right: B): C
    fun invertLeft(value: C): FnResult<A> = error("Not invertible function")
    fun invertRight(value: C): FnResult<B> = error("Not invertible function")
}
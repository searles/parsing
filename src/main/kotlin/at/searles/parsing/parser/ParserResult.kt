package at.searles.parsing.parser

interface ParserResult<A> {
    val isSuccess: Boolean
    val value: A
}
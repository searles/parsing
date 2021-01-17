package at.searles.parsing.parser

interface Fail<A>: ParserResult<A> {
    override val isSuccess: Boolean get() = false
    override val value: A get() = error("No value in failed parser!")
}
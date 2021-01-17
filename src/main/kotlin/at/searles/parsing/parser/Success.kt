package at.searles.parsing.parser

interface Success<A>: ParserResult<A> {
    override val isSuccess: Boolean get() = true
    override val value: A
}
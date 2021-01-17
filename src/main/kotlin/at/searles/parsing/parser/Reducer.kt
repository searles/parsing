package at.searles.parsing.parser

interface Reducer<A, B> {
    fun reduce(stream: ParserStream, input: ParserResult<A>): ParserResult<B>
}
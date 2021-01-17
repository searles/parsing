package at.searles.parsing.parser

interface Conversion<A, B>: Reducer<A, B> {
    override fun reduce(stream: ParserStream, input: ParserResult<A>): ParserResult<B> {
        return stream.createSuccess(convert(input.value))
    }

    fun convert(left: A): B
}
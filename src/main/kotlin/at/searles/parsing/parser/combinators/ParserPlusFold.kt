package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.*

class ParserPlusFold<A, B, C>(private val left: Parser<A>, private val right: Fold<B, A, C>) : Reducer<B, C> {
    override fun reduce(stream: ParserStream, input: ParserResult<B>): ParserResult<C> {
        val inputValue = input.value

        val leftResult = left.parse(stream)

        if(!leftResult.isSuccess) {
            return stream.createFail()
        }

        val rightValue = right.fold(inputValue, leftResult.value)

        return stream.createSuccess(rightValue)
    }

}

package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.ParserPlusReducer
import at.searles.parsing.parser.combinators.RecognizerPlusParser
import at.searles.parsing.parser.combinators.RecognizerToReducer

interface Recognizer {
    fun parse(stream: ParserStream): ParserResult<Nothing?>

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerPlusParser(this, parser)
    }
}
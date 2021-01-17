package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.RecognizerPlusParser

interface Recognizer {
    fun parse(stream: ParserStream): ParserResult<Nothing?>
    val output: String

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerPlusParser(this, parser)
    }
}
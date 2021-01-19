package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.RecognizerPlusParser
import at.searles.parsing.parser.combinators.RecognizerPlusRecognizer
import at.searles.parsing.parser.combinators.RecognizerToReducer

interface Recognizer {
    fun parse(stream: ParserStream): RecognizerResult
    val output: String

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerPlusParser(this, parser)
    }

    operator fun plus(recognizer: Recognizer): Recognizer {
        return RecognizerPlusRecognizer(this, recognizer)
    }

    fun <A> toReducer(): Reducer<A, A> {
        return RecognizerToReducer(this)
    }
}
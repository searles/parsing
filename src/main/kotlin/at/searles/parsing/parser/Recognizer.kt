package at.searles.parsing.parser

import at.searles.parsing.parser.combinators.*
import at.searles.parsing.parser.tools.InitValue
import at.searles.parsing.printer.PrintTree

interface Recognizer {
    fun parse(stream: ParserStream): RecognizerResult
    fun print(): PrintTree

    infix fun or(other: Recognizer): Recognizer {
        return RecognizerOrRecognizer(this, other)
    }

    operator fun plus(recognizer: Recognizer): Recognizer {
        return RecognizerPlusRecognizer(this, recognizer)
    }

    operator fun <A> plus(parser: Parser<A>): Parser<A> {
        return RecognizerPlusParser(this, parser)
    }

    operator fun <A, B> plus(reducer: Reducer<A, B>): Reducer<A, B> {
        return this.toReducer<A>() + reducer
    }

    fun opt(): Recognizer {
        return OptionalRecognizer(this)
    }

    fun flag(): Parser<Boolean> {
        return this.init(true) or InitValue { false }
    }

    fun <A> toReducer(): Reducer<A, A> {
        return RecognizerToReducer(this)
    }

    fun <A> init(value: A): Parser<A> {
        return this + InitValue { value }
    }
}
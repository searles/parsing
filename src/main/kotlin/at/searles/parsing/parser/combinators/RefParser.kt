package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.PrintTree
import kotlin.reflect.KProperty

fun <T> ref(parser: (() -> Parser<T>)): RefParser<T> {
    return RefParser(parser)
}

class RefParser<T>(parser: (() -> Parser<T>)): Parser<T> {

    private val parser: Parser<T> by lazy(parser)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Parser<T> {
        return this
    }

    override fun parse(stream: ParserStream): ParserResult<T> {
        return parser.parse(stream)
    }

    override fun print(value: T): PrintTree {
        return parser.print(value)
    }
}
package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.PrintTree
import kotlin.reflect.KProperty

fun <T> ref(parser: (() -> Parser<T>)): RefParser<T> {
    return RefParser(parser)
}

class RefParser<T>(private val createParser: (() -> Parser<T>)): Parser<T> {
    private var status: Int = UNRESOLVED
    private lateinit var lazyParser: Parser<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Parser<T> {
        return when (status) {
            UNRESOLVED -> {
                status = RESOLVING
                lazyParser = createParser()
                status = RESOLVED
                lazyParser
            }
            RESOLVING -> this
            else -> lazyParser
        }
    }

    override fun parse(stream: ParserStream): ParserResult<T> {
        return lazyParser.parse(stream)
    }

    override fun print(value: T): PrintTree {
        return lazyParser.print(value)
    }

    companion object {
        private const val UNRESOLVED = 0
        private const val RESOLVING = 1
        private const val RESOLVED = 2
    }
}
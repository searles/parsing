package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.printer.PrintTree
import kotlin.reflect.KProperty

fun <T> ref(parser: (() -> Parser<T>)): RefParser<T> {
    return RefParser(null, parser)
}

fun <T> ref(label: String, parser: (() -> Parser<T>)): RefParser<T> {
    return RefParser(label, parser)
}

class RefParser<T>(private val label: String?, private val createParser: (() -> Parser<T>)): Parser<T> {
    private var status: Int = UNRESOLVED
    private lateinit var lazyParser: Parser<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Parser<T> {
        return getValue()
    }

    private fun getValue(): Parser<T> {
        if(status == RESOLVING) {
            return this
        }

        initializeIfUninitialized()

        return if(label == null) lazyParser else this
    }

    private fun initializeIfUninitialized() {
        if(status == UNRESOLVED) {
            status = RESOLVING
            lazyParser = createParser()
            status = RESOLVED
        }
    }

    override fun parse(stream: TokenStream): ParserResult<T> {
        initializeIfUninitialized()
        return lazyParser.parse(stream)
    }

    override fun print(value: T): PrintTree {
        initializeIfUninitialized()
        return lazyParser.print(value)
    }

    override fun toString(): String {
        return label ?: "..."
    }

    companion object {
        private const val UNRESOLVED = 0
        private const val RESOLVING = 1
        private const val RESOLVED = 2
    }
}
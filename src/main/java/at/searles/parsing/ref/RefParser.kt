package at.searles.parsing.ref

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

class RefParser<T>(private val label: String) : Parser<T> {

    var ref: Parser<T>
        get() = internalRef ?: error("$label is not initialized")

        set(value) {
            internalRef = value
        }

    private var internalRef: Parser<T>? = null

    override fun parse(stream: ParserStream): T? {
        return ref.parse(stream)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return ref.recognize(stream)
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return ref.print(item)
    }

    override fun toString(): String {
        return label
    }
}
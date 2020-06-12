package at.searles.parsing

import at.searles.parsing.printing.ConcreteSyntaxTree

interface Initializer<T> : Parser<T> {
    override fun parse(stream: ParserStream): T

    fun consume(t: T): Boolean = false

    override fun print(item: T): ConcreteSyntaxTree? {
        return if (consume(item)) ConcreteSyntaxTree.empty() else null
    }

    override fun recognize(stream: ParserStream): Boolean {
        return true
    }
}
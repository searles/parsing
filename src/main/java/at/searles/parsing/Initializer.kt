package at.searles.parsing

import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree

interface Initializer<T> : Parser<T> {
    override fun parse(stream: ParserStream): T

    fun consume(t: T): Boolean = false

    override fun print(item: T): ConcreteSyntaxTree? {
        return if (consume(item)) EmptyTree else null
    }

    override fun recognize(stream: ParserStream): Boolean {
        return true
    }

    companion object {
        fun <T> create(initializer: () -> T): Initializer<T> {
            return object: Initializer<T> {
                override fun parse(stream: ParserStream): T {
                    return initializer()
                }
            }
        }

        fun <T> create(consumer: (T) -> Boolean, initializer: () -> T): Initializer<T> {
            return object: Initializer<T> {
                override fun parse(stream: ParserStream): T {
                    return initializer()
                }

                override fun consume(t: T): Boolean {
                    return consumer(t)
                }
            }
        }

        fun <T> create(consumer: (T) -> Boolean, initializer: (Trace) -> T): Initializer<T> {
            return object: Initializer<T> {
                override fun parse(stream: ParserStream): T {
                    return initializer(stream.createTrace())
                }

                override fun consume(t: T): Boolean {
                    return consumer(t)
                }
            }
        }
    }
}
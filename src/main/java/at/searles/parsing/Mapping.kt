package at.searles.parsing

import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.PartialConcreteSyntaxTree

interface Mapping<T, U> : Reducer<T, U> {
    override fun parse(stream: ParserStream, input: T): U

    fun left(result: U): T? {
        return null
    }

    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        val left = left(item) ?: return null
        return PartialConcreteSyntaxTree(left, ConcreteSyntaxTree.empty())
    }

    override fun recognize(stream: ParserStream): Boolean {
        return true
    }

    companion object {
        fun <T> identity(): Mapping<T, T> {
            return object: Mapping<T, T> {
                override fun parse(stream: ParserStream, input: T): T = input

                override fun left(result: T): T? = result
            }
        }

        fun <T, U>  create(mapping: (T) -> U): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(stream: ParserStream, input: T): U {
                    return mapping(input)
                }
            }
        }

        fun <T, U>  create(inverse: (U) -> T?, mapping: (T) -> U): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(stream: ParserStream, input: T): U {
                    return mapping(input)
                }

                override fun left(result: U): T? {
                    return inverse(result)
                }
            }
        }

        fun <T, U>  create(inverse: (U) -> T?, mapping: (Trace, T) -> U): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(stream: ParserStream, input: T): U {
                    return mapping(stream.toTrace(), input)
                }

                override fun left(result: U): T? {
                    return inverse(result)
                }
            }
        }

        inline fun <reified T: U, U> cast(): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(stream: ParserStream, input: T): U {
                    return input
                }

                override fun left(result: U): T? {
                    return result as? T
                }
            }
        }
    }
}
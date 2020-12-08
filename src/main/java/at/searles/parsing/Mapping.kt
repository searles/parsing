package at.searles.parsing

import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.PartialTree

interface Mapping<T, U> : Reducer<T, U> {
    override fun parse(left: T, stream: ParserStream): U

    fun left(result: U): T? {
        return null
    }

    override fun print(item: U): PartialTree<T>? {
        val left = left(item) ?: return null
        return PartialTree(left, ConcreteSyntaxTree.empty())
    }

    override fun recognize(stream: ParserStream): Boolean {
        return true
    }

    companion object {
        fun <T> identity(): Mapping<T, T> {
            return object: Mapping<T, T> {
                override fun parse(left: T, stream: ParserStream): T = left

                override fun left(result: T): T? = result
            }
        }

        fun <T, U>  create(mapping: (T) -> U): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(left: T, stream: ParserStream): U {
                    return mapping(left)
                }
            }
        }

        fun <T, U>  create(inverse: (U) -> T?, mapping: (T) -> U): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(left: T, stream: ParserStream): U {
                    return mapping(left)
                }

                override fun left(result: U): T? {
                    return inverse(result)
                }
            }
        }

        fun <T, U>  create(inverse: (U) -> T?, mapping: (Trace, T) -> U): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(left: T, stream: ParserStream): U {
                    return mapping(stream.createTrace(), left)
                }

                override fun left(result: U): T? {
                    return inverse(result)
                }
            }
        }

        inline fun <reified T: U, U> cast(): Mapping<T, U> {
            return object: Mapping<T, U> {
                override fun parse(left: T, stream: ParserStream): U {
                    return left
                }

                override fun left(result: U): T? {
                    return result as? T
                }
            }
        }
    }
}
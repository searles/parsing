package at.searles.parsing.parser.tools

import at.searles.parsing.parser.*
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

object CastBuilders {
    inline fun <reified T> cast(): Cast<T> {
        return Cast()
    }

    inline fun <reified T> castAll(): CastAll<T> {
        return CastAll()
    }

    inline fun <reified T> nullable(): Conversion<T, T?> {
        return object: Conversion<T, T?> {
            override fun convert(value: T): T? {
                return value
            }

            override fun invert(value: T?): FnResult<T> {
                return value?.let { FnResult.success(it) } ?: FnResult.failure
            }

            override fun toString(): String {
                return "(nullable)"
            }
        }

    }

    class Cast<U> {
        inline fun <reified T: U> from(): Conversion<T, U> {
            return object: Conversion<T, U> {
                override fun convert(value: T): U {
                    return value
                }

                override fun print(value: U): PartialPrintTree<T> {
                    return if(value is T)
                        PartialPrintTree.of(value, PrintTree.Empty)
                    else
                        PartialPrintTree.failure
                }

                override fun toString(): String {
                    return "cast(${T::class.simpleName})"
                }
            }
        }
    }

    class CastAll<U> {
        inline fun <reified T: U> from(): Conversion<List<T>, List<U>> {
            return object: Conversion<List<T>, List<U>> {
                override fun convert(value: List<T>): List<U> {
                    return value
                }

                override fun invert(value: List<U>): FnResult<List<T>> {
                    if(value.any { it !is T }) return FnResult.failure
                    @Suppress("UNCHECKED_CAST")
                    return FnResult.success(value as List<T>)
                }

                override fun toString(): String {
                    return "{castAll<${T::class.java.simpleName}>}"
                }
            }
        }
    }

    inline operator fun <reified T: U, U> Parser<T>.plus(cast: Cast<U>): Parser<U> {
        return this + cast.from()
    }

    inline operator fun <T, reified U: V, V> Reducer<T, U>.plus(cast: Cast<V>): Reducer<T, V> {
        return this + cast.from()
    }

    inline operator fun <reified T: U, U> Parser<List<T>>.plus(castAll: CastAll<U>): Parser<List<U>> {
        return this + castAll.from()
    }
}

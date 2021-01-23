package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult

inline fun <reified T: U, U> cast(): Conversion<T, U> {
    return object: Conversion<T, U> {
        override fun convert(value: T): U {
            return value
        }

        override fun invert(value: U): FnResult<T> {
            if(value !is T) return FnResult.failure
            return FnResult.success(value)
        }

        override fun toString(): String {
            return "{cast<${T::class.java.simpleName}>}"
        }
    }
}

inline fun <reified T: U, U> castAll(): Conversion<List<T>, List<U>> {
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

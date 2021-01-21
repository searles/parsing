package at.searles.parsing.parser.combinators

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

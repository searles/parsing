package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult

class CastToNullable<A>: Conversion<A, A?> {
    override fun convert(value: A): A? {
        return value
    }

    override fun invert(value: A?): FnResult<A> {
        return value?.let { FnResult.success(it) } ?: FnResult.failure
    }

    override fun toString(): String {
        return "{castToNullable}"
    }
}

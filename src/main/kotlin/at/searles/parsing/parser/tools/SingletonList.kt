package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult

class SingletonList<A>: Conversion<A, List<A>> {
    override fun convert(value: A): List<A> {
        return listOf(value)
    }

    override fun invert(value: List<A>): FnResult<A> {
        if(value.size != 1) {
            return FnResult.failure
        }

        return FnResult.success(value.first())
    }

    override fun toString(): String {
        return "{list}"
    }
}
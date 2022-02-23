package at.searles.parsing.parser

import kotlin.reflect.KProperty

interface ParserResult<out A> {
    val isSuccess: Boolean
    val value: A
    val startIndex: Long
    val endIndex: Long

    operator fun getValue(thisRef: Any?, property: KProperty<*>): A {
        return value
    }

    companion object {
        fun <T> of(value: T, startIndex: Long, endIndex: Long): ParserResult<T> {
            if(value is Traceable) {
                value.setTrace(startIndex, endIndex)
            }

            return object : ParserResult<T> {
                override val isSuccess: Boolean = true
                override val value: T = value
                override val startIndex: Long = startIndex
                override val endIndex: Long = endIndex
            }
        }

        val failure = object: ParserResult<Nothing> {
            override val isSuccess: Boolean = false
            override val value: Nothing get() = error("no value in failure")
            override val startIndex: Long get() = error("no index in failure")
            override val endIndex: Long get() = error("no length in failure")
        }
    }
}
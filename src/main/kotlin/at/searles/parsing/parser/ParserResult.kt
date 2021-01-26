package at.searles.parsing.parser

interface ParserResult<out A> {
    val isSuccess: Boolean
    val value: A
    val index: Long
    val length: Long

    companion object {
        fun <T> of(value: T, index: Long, length: Long): ParserResult<T> =
            object: ParserResult<T> {
                override val isSuccess: Boolean = true
                override val value: T = value
                override val index: Long = index
                override val length: Long = length
            }

        val failure = object: ParserResult<Nothing> {
            override val isSuccess: Boolean = false
            override val value: Nothing get() = error("no value in failure")
            override val index: Long get() = error("no index in failure")
            override val length: Long get() = error("no length in failure")
        }
    }
}
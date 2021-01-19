package at.searles.parsing.parser

class ParserResult<out A>
    private constructor(private val mValue: Any?, private val mIndex: Long, private val mLength: Long) {
    val isSuccess: Boolean get() = mValue !is Failure

    @Suppress("UNCHECKED_CAST")
    val value: A get() =
        when {
            isSuccess -> mValue as A
            else -> error("No value in failure")
        }

    val index: Long get() =
        when {
            isSuccess -> mIndex
            else -> error("failure")
        }

    val length: Long get() =
        when {
            isSuccess -> mLength
            else -> error("failure")
        }


    private object Failure

    companion object {
        fun <T> success(value: T, index: Long, length: Long): ParserResult<T> = ParserResult(value, index, length)
        @Suppress("UNCHECKED_CAST")
        fun <T> failure(): ParserResult<T> = internalFailure as ParserResult<T>
        private val internalFailure = ParserResult<Any?>(Failure, Long.MIN_VALUE, Long.MIN_VALUE)
    }
}
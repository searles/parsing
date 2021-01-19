package at.searles.parsing.parser

class RecognizerResult
    private constructor(private val mIndex: Long, private val mLength: Long) {
    val isSuccess: Boolean get() = mIndex >= 0

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

    companion object {
        fun success(index: Long, length: Long): RecognizerResult = RecognizerResult(index, length)
        fun failure(): RecognizerResult = internalFailure
        private val internalFailure = RecognizerResult(Long.MIN_VALUE, Long.MIN_VALUE)
    }
}
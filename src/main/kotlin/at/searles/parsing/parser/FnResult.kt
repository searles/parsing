package at.searles.parsing.parser

class FnResult<out A> private constructor(private val mValue: Any?) {
    val isSuccess: Boolean get() = mValue !is Failure

    @Suppress("UNCHECKED_CAST")
    val value: A get() =
        when {
            isSuccess -> mValue as A
            else -> error("No value in failure")
        }

    private object Failure

    companion object {
        fun <T> success(value: T): FnResult<T> = FnResult(value)
        @Suppress("UNCHECKED_CAST")
        fun <T> failure(): FnResult<T> = internalFailure as FnResult<T>
        private val internalFailure = FnResult<Any?>(Failure)
        fun <T> ofNullable(value: T?): FnResult<T> = if(value != null) success(value) else failure()
    }
}
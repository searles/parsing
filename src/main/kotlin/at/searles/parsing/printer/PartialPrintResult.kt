package at.searles.parsing.printer

class PartialPrintResult<out A> private constructor(private val mOutput: String, private val mValue: Any?) {
    val isSuccess: Boolean get() = mValue !is Failure

    @Suppress("UNCHECKED_CAST")
    val value: A get() =
        when {
            isSuccess -> mValue as A
            else -> error("No value in failure")
        }

    val output: String get() =
        when {
            isSuccess -> mOutput
            else -> error("No output in failure")
        }

    private object Failure

    companion object {
        fun <T> success(value: T, output: String): PartialPrintResult<T> = PartialPrintResult(output, value)
        @Suppress("UNCHECKED_CAST")
        fun <T> failure(): PartialPrintResult<T> = internalFailure as PartialPrintResult<T>
        private val internalFailure = PartialPrintResult<Any?>("", Failure)
    }
}
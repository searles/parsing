package at.searles.parsing.printer

class PartialPrintResult<out A> private constructor(private val mValue: Any?, private val mOutput: PrintTree?) {
    val isSuccess: Boolean get() = mValue !is Failure

    @Suppress("UNCHECKED_CAST")
    val value: A get() =
        when {
            isSuccess -> mValue as A
            else -> error("No value in failure")
        }

    val output: PrintTree get() =
        when {
            isSuccess -> mOutput!!
            else -> error("No output in failure")
        }

    private object Failure

    companion object {
        fun <T> success(value: T, output: PrintTree): PartialPrintResult<T> = PartialPrintResult(value, output)
        @Suppress("UNCHECKED_CAST")
        fun <T> failure(): PartialPrintResult<T> = internalFailure as PartialPrintResult<T>
        private val internalFailure = PartialPrintResult<Any?>(Failure, null)
    }
}
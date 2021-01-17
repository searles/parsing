package at.searles.parsing.printer

// TODO refactor.
class PrintResult private constructor(private val mOutput: Any?) {
    val isSuccess: Boolean get() = mOutput !is Failure

    @Suppress("UNCHECKED_CAST")
    val output: String get() =
        when {
            isSuccess -> mOutput as String
            else -> error("No value in failure")
        }

    private object Failure

    companion object {
        fun success(output: String): PrintResult = PrintResult(output)
        fun failure(): PrintResult = internalFailure
        private val internalFailure = PrintResult(Failure)
    }
}
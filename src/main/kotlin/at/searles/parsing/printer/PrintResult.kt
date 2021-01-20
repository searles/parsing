package at.searles.parsing.printer

class PrintResult private constructor(private val mOutput: PrintTree?) {
    val isSuccess: Boolean get() = mOutput != null

    val output: PrintTree get() =
        when {
            isSuccess -> mOutput!!
            else -> error("No value in failure")
        }

    override fun toString(): String {
        return if(isSuccess) mOutput.toString() else "FAILURE"
    }

    companion object {
        fun success(output: PrintTree): PrintResult = PrintResult(output)
        fun failure(): PrintResult = internalFailure
        private val internalFailure = PrintResult(null)
    }
}
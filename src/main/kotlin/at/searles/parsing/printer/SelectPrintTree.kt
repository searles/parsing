package at.searles.parsing.printer

class SelectPrintTree(private val label: Any) : PrintTree {
    override fun print(outStream: OutStream) {
        outStream.mark(label)
    }
}

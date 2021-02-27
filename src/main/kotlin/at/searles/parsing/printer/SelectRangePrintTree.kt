package at.searles.parsing.printer

class SelectRangePrintTree(private val label: Any, private val tree: PrintTree) : PrintTree {
    override fun print(outStream: OutStream) {
        outStream.select(label, tree)
    }
}

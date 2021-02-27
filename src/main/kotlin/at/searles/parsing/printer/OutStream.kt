package at.searles.parsing.printer

interface OutStream {
    fun append(seq: CharSequence)
    fun append(codePoint: Int)
    fun mark(label: Any) {}
    fun select(label: Any, tree: PrintTree) {
        tree.print(this)
    }
}
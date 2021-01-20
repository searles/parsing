package at.searles.parsing.printer

class StringPrintTree(private val string: String) : PrintTree {
    override fun print(outStream: OutStream) {
        outStream.append(string)
    }

    override fun toString(): String {
        return string
    }
}

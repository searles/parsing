package at.searles.parsing.printer

class StringOutStream : OutStream {
    private val sb = StringBuilder()

    override fun append(seq: CharSequence) {
        sb.append(seq)
    }

    override fun append(codePoint: Int) {
        sb.appendCodePoint(codePoint)
    }

    override fun toString(): String {
        return sb.toString()
    }
}
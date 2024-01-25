package at.searles.parsing.printer

interface OutStream {
    fun append(seq: CharSequence) {
        seq.codePoints().forEach {
            append(it)
        }
    }

    fun append(codePoint: Int)
}
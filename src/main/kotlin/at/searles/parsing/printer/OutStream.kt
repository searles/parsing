package at.searles.parsing.printer

interface OutStream {
    fun append(seq: CharSequence)
    fun append(codePoint: Int)
}
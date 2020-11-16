package at.searles.parsing.printing

interface OutStream {
    fun append(seq: CharSequence)
    fun append(codePoint: Int)
}
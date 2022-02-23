package at.searles.parsing.codepoint

interface IndexedStream : CodePointStream {
    /**
     * Contract: with every call to read(), index increases, but possibly by more than one.
     * The delta is up to the underlying implementation (eg a utf-8-stream might increment by the number of bytes).
     * The index is unique for the just-read codepoint.
     */
    val index: Long
    fun reset(newIndex: Long)
    fun getString(startIndex: Long, endIndex: Long): String
    fun getCodePointStream(startIndex: Long, endIndex: Long): CodePointStream

    companion object {
        fun of(stream: CodePointStream, bufferSize: Int = 65536): IndexedStream {
            return BufferedCodePointStream(stream, bufferSize)
        }
    }
}
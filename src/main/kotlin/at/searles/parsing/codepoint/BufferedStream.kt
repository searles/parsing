package at.searles.parsing.codepoint

interface BufferedStream : CodePointStream {
    /**
     * Contract: with every call to read(), index increases.
     * The delta is up to the underlying implementation (eg a utf-8-stream might increment by the number of bytes)
     */
    val index: Long

    /**
     * Contract: newIndex >= index - bufferSize
     */
    fun backtrackToIndex(newIndex: Long)

    /**
     * This method is way faster (speed-up of 40%) compared to getString().
     */
    fun getCharSequence(index: Long, length: Int): CharSequence

    fun getString(index: Long, length: Int): String

    /**
     * For variable char size encodings, this method returns -1 if at the given position
     * there is no valid codepoint.
     */
    fun getCodePointAt(index: Long): Int

    companion object {
        fun of(stream: CodePointStream, bufferSize: Int = 65536): BufferedStream {
            return BufferedStreamImpl(stream, bufferSize)
        }
    }
}
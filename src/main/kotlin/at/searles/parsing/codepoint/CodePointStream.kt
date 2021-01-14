package at.searles.parsing.codepoint

interface CodePointStream {
    /**
     * Returns the next char from this stream (unicode).
     * This method considers chars beyond the 65536-limit in
     * unicode.
     *
     * @return -1 if there are no more elements.
     */
    fun read(): Int
}
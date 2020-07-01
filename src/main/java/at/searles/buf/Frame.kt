package at.searles.buf

interface Frame : CharSequence {
    /**
     * The pointer to the current frame start.
     */
    fun startPosition(): Long

    /**
     * The pointer to the current frame start.
     */
    fun endPosition(): Long
}
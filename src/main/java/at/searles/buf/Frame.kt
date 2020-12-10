package at.searles.buf

import at.searles.parsing.Trace

interface Frame : CharSequence {
    /**
     * The pointer to the current frame start.
     */
    val start: Long

    /**
     * The pointer to the current frame end (exclusive).
     */
    val end: Long

    fun createTrace(): Trace {
        return FrameStreamTrace(this)
    }

    class FrameStreamTrace(frame: Frame) : Trace {
        override val start: Long = frame.start
        override val end: Long = frame.end
    }

}
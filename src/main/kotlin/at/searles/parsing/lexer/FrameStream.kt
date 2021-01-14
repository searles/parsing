package at.searles.parsing.lexer

import at.searles.parsing.codepoint.BufferedStream

class FrameStream(private val stream: BufferedStream) {

    private var frameStart = stream.index
    private var frameEnd = stream.index

    val frame = object: Frame {
        override val start: Long get() = frameStart
        override val end: Long get() = frameEnd
        override fun toString(): String {
            return stream.substring(start, end)
        }
    }

    fun read(): Int {
        return stream.read()
    }

    fun setFrameEnd() {
        frameEnd = stream.index
    }

    fun consumeFrame() {
        frameStart = frameEnd
        stream.backtrackToIndex(frameEnd)
    }

    interface Frame {
        val start: Long
        val end: Long
    }
}
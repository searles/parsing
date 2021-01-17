package at.searles.parsing.lexer

import at.searles.parsing.codepoint.BufferedStream
import at.searles.parsing.codepoint.ReaderCodePointStream
import at.searles.parsing.codepoint.StringCodePointStream
import java.io.Reader

class FrameStream(private val stream: BufferedStream) {
    constructor(string: String): this(StringCodePointStream(string))
    constructor(reader: Reader): this(BufferedStream.Impl(ReaderCodePointStream(reader)))

    private var frameIndex = stream.index
    private var frameLength = 0L

    val frame = object: Frame {
        override val index: Long get() = frameIndex
        override val length: Long get() = frameLength
        override val string: String get() {
            return stream.substring(index, index + length)
        }

        override fun toString(): String {
            return string
        }
    }

    fun read(): Int {
        return stream.read()
    }

    fun setFrameEnd() {
        frameLength = stream.index - frame.index
    }

    fun consumeFrame() {
        frameIndex += frameLength
        resetFrame()
    }

    fun resetFrame() {
        frameLength = 0
        stream.backtrackToIndex(frameIndex)
    }

}
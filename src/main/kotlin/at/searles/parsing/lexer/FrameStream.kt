package at.searles.parsing.lexer

import at.searles.parsing.codepoint.BufferedStream
import at.searles.parsing.codepoint.ReaderCodePointStream
import at.searles.parsing.codepoint.StringCodePointStream
import java.io.Reader

class FrameStream(private val stream: BufferedStream) {
    constructor(string: String): this(StringCodePointStream(string))
    constructor(reader: Reader): this(BufferedStream.Impl(ReaderCodePointStream(reader)))

    private var frameIndex = stream.index
    private var frameLength = 0

    val frame = object: Frame { // TODO make internal class
        override val index: Long get() = frameIndex
        override val length: Int get() = frameLength

        override fun get(index: Int): Char {
            TODO("Not yet implemented")
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            return stream.substring(index, index + length)
        }
    }

    fun read(): Int {
        return stream.read()
    }

    fun setFrameEnd() {
        frameLength = (stream.index - frame.index).toInt()
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
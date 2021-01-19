package at.searles.parsing.lexer

import at.searles.parsing.codepoint.BufferedStream
import at.searles.parsing.codepoint.ReaderCodePointStream
import at.searles.parsing.codepoint.StringCodePointStream
import java.io.Reader

class FrameStream(private val stream: BufferedStream) {
    constructor(string: String): this(StringCodePointStream(string))
    constructor(reader: Reader): this(BufferedStream.Impl(ReaderCodePointStream(reader)))

    var frameIndex = stream.index
        private set
    var frameLength = 0
        private set

    fun read(): Int {
        return stream.read()
    }

    fun getFrame(): String {
        return stream.getString(frameIndex, frameLength)
    }

    fun setFrameEnd() {
        frameLength = (stream.index - frameIndex).toInt()
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
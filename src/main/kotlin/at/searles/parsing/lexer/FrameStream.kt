package at.searles.parsing.lexer

import at.searles.parsing.codepoint.BufferedStream
import at.searles.parsing.codepoint.ReaderCodePointStream
import at.searles.parsing.codepoint.StringCodePointStream
import java.io.Reader

class FrameStream(private val stream: BufferedStream) {
    constructor(string: String): this(StringCodePointStream(string))
    constructor(reader: Reader): this(BufferedStream.of(ReaderCodePointStream(reader)))

    var frameIndex: Long = stream.index
        private set
    var frameLength: Long = 0L
        private set

    fun read(): Int {
        return stream.read()
    }

    fun getFrame(): CharSequence {
        return stream.getCharSequence(frameIndex, frameLength.toInt())
    }

    fun setFrameEnd() {
        frameLength = stream.index - frameIndex
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
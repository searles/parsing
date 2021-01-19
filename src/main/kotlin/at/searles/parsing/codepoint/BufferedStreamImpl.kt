package at.searles.parsing.codepoint

import java.lang.StringBuilder

class BufferedStreamImpl(private val stream: CodePointStream, bufferSize: Int) : BufferedStream {
    init {
        require(bufferSize > 0)
    }

    override var index = 0L

    private val buffer: IntArray = IntArray(bufferSize)
    private var streamIndex = 0L

    override fun backtrackToIndex(newIndex: Long) {
        checkIndexInBuffer(newIndex)
        index = newIndex
    }

    override fun getCharSequence(index: Long, length: Int): CharSequence {
        checkIndexInBuffer(index)
        checkIndexInBuffer(index + length - 1)

        return CharSeq(index, length, this)
    }

    override fun getString(index: Long, length: Int): String {
        checkIndexInBuffer(index)
        checkIndexInBuffer(index + length - 1)

        val sb = StringBuilder()

        for(i in 0 until length) {
            sb.appendCodePoint(getCodePointAtUnsafe(index + i))
        }

        return sb.toString()
    }

    private fun getCodePointAtUnsafe(index: Long): Int {
        return buffer[(index % buffer.size).toInt()]
    }

    override fun getCodePointAt(index: Long): Int {
        checkIndexInBuffer(index)
        return getCodePointAtUnsafe(index)
    }

    private fun checkIndexInBuffer(newIndex: Long) {
        if (streamIndex - newIndex !in 0..buffer.size) {
            throw OutOfBufferRangeException("Cannot backtrack from $streamIndex to $newIndex because buffer size is ${buffer.size}")
        }
    }

    private val bufferIndex get() = (index % buffer.size).toInt()

    override fun read(): Int {
        if(index < streamIndex) {
            return readFromBuffer()
        }

        require(index == streamIndex)

        return readFromStream()
    }

    private fun readFromBuffer(): Int {
        val codePoint = buffer[bufferIndex]
        index ++
        return codePoint
    }

    private fun readFromStream(): Int {
        val codePoint = stream.read()

        if(codePoint == -1) return -1

        buffer[bufferIndex] = codePoint

        streamIndex ++
        index ++

        return codePoint
    }

    override fun toString(): String {
        return "$stream[$index]"
    }
}
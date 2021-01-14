package at.searles.parsing.codepoint

import java.lang.StringBuilder

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

    fun substring(start: Long, end: Long): String

    class Impl(private val stream: CodePointStream, bufferSize: Int = 65535) : BufferedStream {
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

        override fun substring(start: Long, end: Long): String {
            require(start <= end)
            checkIndexInBuffer(start)
            checkIndexInBuffer(end)

            val sb = StringBuilder()

            for(i in start until end) {
                sb.appendCodePoint(getCodePointAt(i))
            }

            return sb.toString()
        }

        private fun getCodePointAt(index: Long) = buffer[(index % buffer.size).toInt()]

        private fun checkIndexInBuffer(newIndex: Long) {
            if (streamIndex - newIndex !in 0..buffer.size) {
                throw BufferTooSmallException("Cannot backtrack from $streamIndex to $index because buffer size is ${buffer.size}")
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
}
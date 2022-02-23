package at.searles.parsing.codepoint

import kotlin.text.StringBuilder

class BufferedCodePointStream(private val delegate: CodePointStream, bufferSize: Int = 65536) : IndexedStream {
    init {
        require(bufferSize > 0)
    }

    override var index = 0L
    private var count = 0L
    private val buffer: IntArray = IntArray(bufferSize)

    override fun read(): Int {
        val bufferIndex = getBufferIndex(index)

        if(index < count) {
            // it is inside the buffer
            index ++
            return buffer[bufferIndex]
        }

        require(index == count)

        val nextCodePoint = delegate.read()
        if(nextCodePoint == -1) {
            return -1
        }

        index++
        count++
        buffer[bufferIndex] = nextCodePoint

        return nextCodePoint
    }

    override fun reset(newIndex: Long) {
        if(newIndex < count) checkIndexInBuffer(newIndex)
        index = newIndex
    }

    override fun getString(startIndex: Long, endIndex: Long): String {
        // endIndex is exclusive
        val sb = StringBuilder((endIndex - startIndex).toInt())
        val stream = getCodePointStream(startIndex, endIndex)

        var codePoint: Int = stream.read()
        while (codePoint != -1) {
            sb.appendCodePoint(codePoint)
            codePoint = stream.read()
        }

        return sb.toString()
    }

    override fun getCodePointStream(startIndex: Long, endIndex: Long): CodePointStream {
        checkIndexInBuffer(startIndex)
        if(endIndex > 0) checkIndexInBuffer(endIndex - 1) // endIndex is exclusive.

        return object: CodePointStream {
            var index = startIndex
            override fun read(): Int {
                return if(index >= endIndex) -1
                else buffer[getBufferIndex(index++)]
            }
        }
    }

    private fun checkIndexInBuffer(checkedIndex: Long) {
        if(checkedIndex >= count || checkedIndex < count - buffer.size || checkedIndex < 0) {
            throw OutOfBufferRangeException("Buffer size insufficient")
        }
    }

    private fun getBufferIndex(index: Long): Int {
        return (index % buffer.size.toLong()).toInt()
    }
}
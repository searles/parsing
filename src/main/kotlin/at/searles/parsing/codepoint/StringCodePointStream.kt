package at.searles.parsing.codepoint

/**
 * This class creates a CharStream out of a Reader.
 */
class StringCodePointStream(private val string: String) : BufferedStream {
    override var index = 0L
        private set

    override fun backtrackToIndex(newIndex: Long) {
        index = newIndex
    }

    override fun substring(start: Long, end: Long): String {
        return string.substring(start.toInt(), end.toInt())
    }

    override fun read(): Int {
        return if(index < string.length) readNextCodePoint() else -1
    }

    private fun readNextCodePoint(): Int {
        val codePoint = string.codePointAt(index.toInt())
        index += Character.charCount(codePoint)
        return codePoint
    }

    override fun toString(): String {
        return string.substring(0 until index.toInt()) + "_" + string.substring(index.toInt())
    }

}
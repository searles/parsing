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

    override fun getCharSequence(index: Long, length: Int): CharSequence {
        return getString(index, length)
    }

    override fun getString(index: Long, length: Int): String {
        return string.substring(index.toInt(), index.toInt() + length)
    }

    override fun getCodePointAt(index: Long): Int {
        if(Character.isLowSurrogate(string[index.toInt()])) {
            return -1
        }

        return string.codePointAt(index.toInt())
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
package at.searles.parsing.codepoint

/**
 * This class creates a CharStream out of a Reader.
 */
class StringCodePointStream(private val string: String) : IndexedStream {
    override var index = 0L
        private set

    override fun read(): Int {
        return if(index < string.length) readNextCodePoint() else -1
    }

    private fun readNextCodePoint(): Int {
        val codePoint = string.codePointAt(index.toInt())
        index += Character.charCount(codePoint)
        return codePoint
    }

    override fun reset(newIndex: Long) {
        index = newIndex
    }

    override fun getString(startIndex: Long, endIndex: Long): String {
        return string.substring(startIndex.toInt(), endIndex.toInt())
    }

    override fun getCodePointStream(startIndex: Long, endIndex: Long): CodePointStream {
        return object: CodePointStream {
            var index = startIndex.toInt()
            override fun read(): Int {
                if(index >= endIndex) {
                    return -1
                }

                return string.codePointAt(index).also {
                    index += Character.charCount(it)
                }
            }
        }
    }
}
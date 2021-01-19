package at.searles.parsing.codepoint

class CharSeq(private val streamIndex: Long, override val length: Int, private val stream: BufferedStream): CharSequence {
        override fun get(index: Int): Char {
            return stream.getCodePointAt(streamIndex + index).toChar() // TODO Check for overflow
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return toString().subSequence(startIndex, endIndex)
        }

        override fun toString(): String {
            return stream.getString(streamIndex, length)
        }
    }

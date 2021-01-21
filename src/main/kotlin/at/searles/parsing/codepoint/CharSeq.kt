package at.searles.parsing.codepoint

import java.text.CharacterIterator

class CharSeq(private val streamIndex: Long, override val length: Int, private val stream: BufferedStream): CharSequence {
        override fun get(index: Int): Char {
            val cp = stream.getCodePointAt(streamIndex + index)

            if(cp < 0 || cp > Char.MAX_VALUE.toInt()) {
                error("Out of range")
            }

            return cp.toChar()
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return toString().subSequence(startIndex, endIndex)
        }

        override fun toString(): String {
            return stream.getString(streamIndex, length)
        }
    }

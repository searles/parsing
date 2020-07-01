package at.searles.buf

import java.io.IOException
import java.io.Reader

/**
 * This class creates a CharStream out of a Reader.
 */
class ReaderCharStream(private val r: Reader) : CharStream {

    private var offset: Long = 0

    override fun next(): Int {
        return try {
            val ch = r.read()
            if (ch == -1) {
                return -1
            }
            offset++
            if (Character.isHighSurrogate(ch.toChar())) {
                val low = r.read()
                require(low != -1) { "no lo surrogate from this reader" }
                offset++
                return Character.toCodePoint(ch.toChar(), low.toChar())
            }
            ch // char = codepoint.
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    override fun toString(): String {
        return "$r@$offset"
    }

}
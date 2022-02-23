package at.searles.parsing.codepoint

import java.io.IOException
import java.io.Reader

/**
 * This class creates a CharStream out of a Reader.
 */
class ReaderCodePointStream(private val reader: Reader) : CodePointStream {
    override fun read(): Int {
        return try {
            tryRead()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    private fun tryRead(): Int {
        val ch = reader.read()

        return when {
            ch == -1 -> -1
            Character.isHighSurrogate(ch.toChar()) -> readAndCombineSurrogates(ch.toChar())
            else -> ch
        }
    }

    private fun readAndCombineSurrogates(hi: Char): Int {
        val lo = reader.read()

        if(lo < 0) throw ReaderCodePointException("Missing low surrogate")

        if(!Character.isLowSurrogate(lo.toChar())) {
            throw ReaderCodePointException("No low surrogate after high surrogate")
        }

        return Character.toCodePoint(hi, lo.toChar())
    }

    override fun toString(): String {
        return reader.toString()
    }
}
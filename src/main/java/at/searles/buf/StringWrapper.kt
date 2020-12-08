package at.searles.buf

import kotlin.math.max
import kotlin.math.min

class StringWrapper(private val charSequence: CharSequence) : FrameStream {
    override val frame: Frame = CharSeq()

    private var ptr = 0
    private var frameStart = 0
    private var frameEnd = 0

    override var position: Long
        get() {
            return ptr.toLong()
        }
        set(value) {
            this.ptr = value.toInt()
            frameEnd = this.ptr
            frameStart = frameEnd
        }

    private fun codePointAt(index: Int): Int {
        val c1 = charSequence[index]

        if (Character.isHighSurrogate(c1) && index < charSequence.length - 1) {
            val c2 = charSequence[index + 1]

            if (Character.isLowSurrogate(c2)) {
                return Character.toCodePoint(c1, c2)
            }
        }

        return c1.toInt()
    }

    override fun next(): Int {
        if (ptr >= charSequence.length) {
            return -1
        }
        val ch = codePointAt(ptr)
        ptr += Character.charCount(ch)
        return ch
    }

    override fun mark() {
        assert(ptr >= frameStart)
        frameEnd = ptr
    }

    override fun advance() {
        frameStart = frameEnd
        ptr = frameStart
    }

    override fun reset() {
        frameEnd = frameStart
        ptr = frameEnd
    }

    override fun toString(): String {
        return (charSequence.subSequence(max(0, frameStart - 16), frameStart).toString() + "_" +
                charSequence.subSequence(frameStart, frameEnd) + "_" +
                charSequence.subSequence(frameEnd, min(charSequence.length, frameEnd + 16)))
    }

    private inner class CharSeq : Frame {
        override val start: Long get() = frameStart.toLong()

        override val end: Long get() = frameEnd.toLong()

        override val length: Int get() {
            return frameEnd - frameStart
        }

        override fun get(index: Int): Char {
            return charSequence[index + frameStart]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return charSequence.subSequence(frameStart + startIndex, frameStart + endIndex)
        }

        override fun toString(): String {
            return charSequence.subSequence(frameStart, frameEnd).toString()
        }
    }
}
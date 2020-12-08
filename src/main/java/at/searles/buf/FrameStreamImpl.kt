package at.searles.buf

class FrameStreamImpl(private val stream: BufferedStream.Impl) : FrameStream {
    override val frame: Frame = CharSeq()
    private var frameStart: Long = 0
    private var frameEnd: Long = 0
    private var invalid = false

    override fun next(): Int {
        // is the frame too big?
        require(frameEnd - frameStart < stream.bufSize()) { "buffer size is too small" }
        return stream.next()
    }

    override var position: Long
        get() {
            return stream.position
        }
        set(value) {
            invalid = true
            stream.position = value
            frameStart = value
            frameEnd = value
        }

    override fun mark() {
        invalid = true
        frameEnd = stream.position
    }

    override fun advance() {
        invalid = true
        frameStart = frameEnd
        stream.position = frameEnd
    }

    override fun reset() {
        invalid = true
        frameEnd = frameStart
        stream.position = frameStart
    }

    private inner class CharSeq : Frame {
        private lateinit var str: String

        private fun update() {
            if (invalid) {
                val sb = StringBuilder()

                for (index in frameStart until frameEnd) {
                    sb.appendCodePoint(stream.cached(index))
                }

                str = sb.toString()
                invalid = false
            }
        }

        override val start: Long get() = frameStart

        override val end: Long get() = frameEnd
        override val length: Int get() {
            update()
            return str.length
        }

        override fun get(index: Int): Char {
            update()
            return str[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            update()
            return str.subSequence(startIndex, endIndex)
        }

        override fun toString(): String {
            update()
            return str
        }
    }
}
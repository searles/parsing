package at.searles.buf

interface BufferedStream : CharStream {
    /**
     * @return A pointer that uniquely identifies the
     * next char that is returned by next(). Most likely
     * the index of the underlying code unit like index
     * of charAt() in a string.
     */
    var position: Long

    class Impl(private val stream: CharStream, bufSize: Int) : BufferedStream {
        private val buffer: IntArray = IntArray(bufSize)
        private var bufferPtr: Int = 0
        private var ptr: Long = 0

        override var position: Long
            get() = ptr
            set(value) {
                checkIfPtrInCache(value)
                ptr = value
            }


        fun bufSize(): Int {
            return buffer.size
        }

        fun cached(ptr: Long): Int {
            checkIfPtrInCache(ptr)
            return buffer[(ptr % buffer.size).toInt()]
        }

        override fun next(): Int {
            if (ptr % buffer.size != bufferPtr.toLong()) {
                return buffer[(ptr++ % buffer.size).toInt()]
            }

            // next one is a new one.
            val ch = stream.next()

            if (ch == -1) {
                return -1
            }

            buffer[bufferPtr] = ch
            bufferPtr = (bufferPtr + 1) % buffer.size
            ptr++
            return ch
        }

        private fun checkIfPtrInCache(ptr: Long) {
            require(this.position >= ptr) { "cannot foresee future" }
            require(this.position - ptr < buffer.size) { "buffer is too small, Maximum size is ${buffer.size}" }
        }

    }
}
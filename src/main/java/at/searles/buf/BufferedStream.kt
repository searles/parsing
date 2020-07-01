package at.searles.buf

interface BufferedStream : CharStream {
    /**
     * @return A pointer that uniquely identifies the
     * next char that is returned by next(). Most likely
     * the index of the underlying code unit like index
     * of charAt() in a string.
     */
    fun position(): Long

    /**
     * Sets the pointer position to the argument. The underlying
     * implementation must specify the maximum difference between
     * position() and the argument here.
     *
     * @param ptr The pointer is reverted
     * to the corresponding position.
     */
    fun setPositionTo(ptr: Long)

    class Impl(private val stream: CharStream, bufSize: Int) : BufferedStream {
        private val buffer: IntArray = IntArray(bufSize)
        private var ptr: Long = 0
        private var offset: Int = 0

        fun bufSize(): Int {
            return buffer.size
        }

        fun cached(ptr: Long): Int {
            checkIfPtrInCache(ptr)
            return buffer[(ptr % buffer.size).toInt()]
        }

        override fun next(): Int {
            if (ptr % buffer.size == offset.toLong()) {
                // next one is a new one.
                val ch = stream.next()
                if (ch == -1) {
                    return -1
                }
                buffer[offset] = ch
                offset = (offset + 1) % buffer.size
                ptr++
                return ch
            }
            return buffer[(ptr++ % buffer.size).toInt()]
        }

        override fun position(): Long {
            return ptr
        }

        private fun checkIfPtrInCache(ptr: Long) {
            require(this.ptr >= ptr) { "cannot foresee future" }
            require(this.ptr - ptr < buffer.size) { "buffer is too small" }
        }

        override fun setPositionTo(ptr: Long) {
            checkIfPtrInCache(ptr)
            this.ptr = ptr
        }
    }
}
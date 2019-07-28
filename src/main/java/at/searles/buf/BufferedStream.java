package at.searles.buf;

public interface BufferedStream extends CharStream {
    /**
     * @return A pointer that uniquely identifies the
     * next char that is returned by next(). Most likely
     * the index of the underlying code unit like index
     * of charAt() in a string.
     */
    long ptr();

    /**
     * Sets the pointer position to the argument. The underlying
     * implementation must specify the maximum difference between
     * ptr() and the argument here.
     *
     * @param ptr The pointer is reverted
     *            to the corresponding position.
     */
    void setPtr(long ptr);

    class Impl implements BufferedStream {

        private final CharStream stream;
        private final int[] buffer;

        private long ptr;
        private int offset;

        public Impl(CharStream stream, int bufSize) {
            this.stream = stream;
            this.buffer = new int[bufSize];
            this.ptr = 0;
            this.offset = 0;
        }

        public int bufSize() {
            return buffer.length;
        }

        public int cached(long ptr) {
            checkIfPtrInCache(ptr);
            return buffer[(int) (ptr % buffer.length)];
        }

        @Override
        public int next() {
            if (ptr % buffer.length == offset) {
                // next one is a new one.
                int ch = stream.next();

                if (ch == -1) {
                    return -1;
                }

                buffer[offset] = ch;
                offset = (offset + 1) % buffer.length;
                ptr++;

                return ch;
            }

            return buffer[(int) (ptr++ % buffer.length)];
        }

        @Override
        public long ptr() {
            return ptr;
        }

        private void checkIfPtrInCache(long ptr) {
            if (this.ptr < ptr) {
                throw new IllegalArgumentException("cannot foresee future");
            }

            if (this.ptr - ptr >= buffer.length) {
                throw new IllegalArgumentException("buffer is too small");
            }
        }

        @Override
        public void setPtr(long ptr) {
            checkIfPtrInCache(ptr);
            this.ptr = ptr;
        }
    }
}

package at.searles.parsing.codepoint

import java.io.Reader

class FrameStream(private val delegate: IndexedStream): IndexedStream by delegate {
    constructor(string: String): this(StringCodePointStream(string))
    constructor(reader: Reader): this(IndexedStream.of(ReaderCodePointStream(reader)))

    var startIndex: Long = delegate.index
        private set
    var endIndex: Long = delegate.index
        private set

    var isReset: Boolean = true
        private set

    val frame: Frame = InnerFrame()

    override fun read(): Int {
        isReset = false
        return delegate.read()
    }

    fun mark() {
        endIndex = index
    }

    fun next() {
        require(endIndex >= startIndex)
        reset(endIndex)
    }

    fun reset() {
        reset(startIndex)
    }

    override fun reset(newIndex: Long) {
        delegate.reset(newIndex)
        startIndex = newIndex
        endIndex = newIndex
        this.isReset = true
    }

    private inner class InnerFrame: Frame {
        override fun getCodePointStream(): CodePointStream {
            return delegate.getCodePointStream(startIndex, endIndex)
        }

        override fun toString(): String {
            return delegate.getString(startIndex, endIndex)
        }

        override val length: Int
            get() = (endIndex - startIndex).toInt()

        override fun get(index: Int): Char {
            TODO("Not yet implemented")
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            TODO("Not yet implemented")
        }
    }
}
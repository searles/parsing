package at.searles.parsing

import at.searles.buf.CharStream
import at.searles.buf.Frame
import at.searles.buf.ReaderCharStream
import at.searles.lexer.TokenStream
import at.searles.lexer.Tokenizer
import java.io.Reader

class ParserStream private constructor(private val tokenStream: TokenStream) {

    /**
     * Marks the start of the current parsed element.
     */
    var start: Long = tokenStream.offset
        private set

    /**
     * Marks the end of the current parsed element.
     */
    var end: Long = tokenStream.offset
        private set

    var maxStatus: BacktrackingStatus? = null
        private set

    var listener: Listener? = null

    /**
     * Returns the position from which the next token will be consumed
     */
    val offset: Long
        get() = tokenStream.offset


    /**
     * Sets the underlying stream to the given offset, ie,
     * the next token matched will start at the given parameter.
     * The caller must take care of start and end of the parsed
     * unit.
     *
     * @param offset The new offset.
     */
    fun restoreOffsetIfNecessary(offset: Long, failedParser: Any) {
        if(offset == this.offset) {
            // some empty parser failed. We are generous with them.
            return
        }

        if(maxStatus == null || maxStatus!!.requestedOffset < offset) {
            maxStatus = BacktrackingStatus(failedParser, offset, this)
        }

        tokenStream.offset = offset
    }

    fun createTrace(): Trace {
        return ParserStreamTrace(this)
    }

    fun parseToken(tokenizer: Tokenizer, tokenId: Int): Frame? {
        val frame = tokenizer.matchToken(tokenStream, tokenId)

        if (frame != null) {
            start = frame.start
            end = frame.end
        }

        return frame
    }

    fun recognize(recognizer: Recognizer, isLeftMost: Boolean = true): Boolean {
        listener?.onParserStart(this)

        val offset0 = offset
        val start0 = start
        val end0 = end

        if(recognizer.recognize(this)) {
            if(!isLeftMost) {
                start = start0
            }

            listener?.onParserSuccess(this)

            return true
        }

        start = start0
        end = end0
        restoreOffsetIfNecessary(offset0, recognizer)

        listener?.onParserFail(this)

        return false
    }

    fun recognize(recognizer: Parser<*>, isLeftMost: Boolean = true): Boolean {
        listener?.onParserStart(this)

        val offset0 = offset
        val start0 = start
        val end0 = end

        if(recognizer.recognize(this)) {
            if(!isLeftMost) {
                start = start0
            }

            listener?.onParserSuccess(this)

            return true
        }

        start = start0
        end = end0
        restoreOffsetIfNecessary(offset0, recognizer)

        listener?.onParserFail(this)

        return false
    }

    fun recognize(recognizer: Reducer<*, *>): Boolean {
        listener?.onParserStart(this)

        val offset0 = offset
        val start0 = start
        val end0 = end

        val status = recognizer.recognize(this)

        start = start0

        if(status) {
            listener?.onParserSuccess(this)
            return true
        }

        end = end0
        restoreOffsetIfNecessary(offset0, recognizer)

        listener?.onParserFail(this)

        return false
    }

    fun <T> parse(parser: Parser<T>, isLeftMost: Boolean= true): T? {
        listener?.onParserStart(this)

        val offset0 = offset
        val start0 = start
        val end0 = end

        val value = parser.parse(this)

        if(value != null) {
            if(!isLeftMost) {
                start = start0
            }

            listener?.onParserSuccess(this)

            return value
        }

        start = start0
        end = end0
        restoreOffsetIfNecessary(offset0, parser)

        listener?.onParserFail(this)

        return value
    }

    fun <T, U> reduce(left: T, reducer: Reducer<T, U>): U? {
        listener?.onParserStart(this)

        val offset0 = offset
        val start0 = start
        val end0 = end

        val value = reducer.parse(left, this)

        start = start0 // position includes left

        if(value != null) {
            listener?.onParserSuccess(this)

            return value
        }

        end = end0
        restoreOffsetIfNecessary(offset0, reducer)

        listener?.onParserFail(this)

        return value
    }

    override fun toString(): String {
        return "$tokenStream: [$start, $end]"
    }

    fun notifyMark(marker: Any) {
        listener?.onMark(marker, this)
    }

    interface Listener {
        fun onMark(marker: Any, stream: ParserStream)
        fun onToken(tokenId: Int, frame: Frame, stream: ParserStream)
        fun onParserStart(stream: ParserStream)
        fun onParserSuccess(stream: ParserStream)
        fun onParserFail(stream: ParserStream)
    }

    class ParserStreamTrace(val stream: ParserStream) : Trace {
        override val start: Long = stream.start
        override val end: Long = stream.end

        override fun toString(): String {
            return "[$start : $end]"
        }
    }

    companion object {
        fun create(seq: CharSequence): ParserStream {
            return ParserStream(TokenStream.fromString(seq)).also {
                it.tokenStream.listener = object: TokenStream.Listener {
                    override fun onToken(tokenId: Int, frame: Frame, stream: TokenStream) {
                        it.listener?.onToken(tokenId, frame, it)
                    }
                }
            }
        }

        fun create(reader: Reader): ParserStream {
            return create(ReaderCharStream(reader))
        }

        fun create(stream: CharStream): ParserStream {
            return ParserStream(TokenStream.fromCharStream(stream)).also {
                it.tokenStream.listener = object: TokenStream.Listener {
                    override fun onToken(tokenId: Int, frame: Frame, stream: TokenStream) {
                        it.listener?.onToken(tokenId, frame, it)
                    }
                }
            }
        }
    }
}
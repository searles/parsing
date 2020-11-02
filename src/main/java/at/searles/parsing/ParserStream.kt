package at.searles.parsing

import at.searles.buf.Frame
import at.searles.lexer.TokenStream
import at.searles.lexer.Tokenizer

class ParserStream(private val stream: TokenStream) {

    /**
     * Marks the start of the current parsed element.
     */
    var start: Long = stream.offset

    /**
     * Marks the end of the current parsed element.
     */
    var end: Long = stream.offset

    var listener: Listener? = null

    var isBacktrackAllowed: Boolean = true

    var maxStatus: BacktrackingStatus? = null

    fun tokStream(): TokenStream {
        return stream
    }

    fun toTrace(): Trace {
        return ParserStreamTrace(this)
    }

    fun parseToken(tokenizer: Tokenizer, tokId: Int): Frame? {
        val frame = tokenizer.matchToken(stream, tokId)

        if (frame != null) {
            start = frame.start
            end = frame.end
        }

        return frame
    }

    /**
     * Sets the underlying stream to the given offset, ie,
     * the next token matched will start at the given parameter.
     * The caller must take care of start and end of the parsed
     * unit.
     *
     * @param offset The new offset.
     */
    fun requestBacktrackToOffset(failedParser: Recognizable.Then, offset: Long) {
        if(offset == this.offset) {
            // some empty parser failed. We are generous with them.
            return
        }

        val trace = BacktrackingStatus(failedParser, offset, this)

        if(!isBacktrackAllowed) {
            throw BacktrackNotAllowedException(trace)
        }

        if(maxStatus == null || maxStatus!!.requestedOffset < offset) {
            maxStatus = trace
        }

        stream.setPositionTo(offset)
    }

    /**
     * Returns the position from which the next token will be consumed
     */
    val offset: Long
        get() = stream.offset

    override fun toString(): String {
        return "$stream: [$start, $end]"
    }

    fun fireRefStart(label: String) {
        if (listener != null) {
            listener!!.onRefStart(this, label)
        }
    }

    fun fireRefSuccess(label: String) {
        if (listener != null) {
            listener!!.onRefSuccess(this, label)
        }
    }

    fun fireRefFail(label: String) {
        if (listener != null) {
            listener!!.onRefFail(this, label)
        }
    }

    interface Listener {
        /**
         * If an annotation starts, the parser after it not necessarily succeeds
         * even in an LL1-Grammar. Yet, all calls to this method are
         * followed by a call to annotationEnd. Hence, all changes done
         * in this method must be undone if the arguments to annotationEnd
         * indicate that the annotation parser did not succeed.
         */
        fun onRefStart(parserStream: ParserStream, label: String)
        fun onRefFail(parserStream: ParserStream, label: String)
        fun onRefSuccess(parserStream: ParserStream, label: String)
    }

    interface SimpleListener : Listener {
        override fun onRefStart(parserStream: ParserStream, label: String) {
            // ignore
        }

        override fun onRefFail(parserStream: ParserStream, label: String) {
            // ignore fails.
        }

        override fun onRefSuccess(parserStream: ParserStream, label: String) {
            onRef(parserStream, label)
        }

        fun onRef(parserStream: ParserStream, label: String)
    }

    class ParserStreamTrace(val stream: ParserStream) : Trace {
        override val start: Long = stream.start
        override val end: Long = stream.end
    }

    companion object {
        fun create(seq: CharSequence): ParserStream {
            return ParserStream(TokenStream.fromString(seq))
        }
    }
}
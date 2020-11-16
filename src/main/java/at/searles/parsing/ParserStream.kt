package at.searles.parsing

import at.searles.buf.Frame
import at.searles.lexer.TokenStream
import at.searles.lexer.Tokenizer

class ParserStream(val tokenStream: TokenStream) {

    /**
     * Marks the start of the current parsed element.
     */
    var start: Long = tokenStream.offset

    /**
     * Marks the end of the current parsed element.
     */
    var end: Long = tokenStream.offset

    var listener: Listener? = null
    var isBacktrackAllowed: Boolean = true
    var maxStatus: BacktrackingStatus? = null

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

        tokenStream.setPositionTo(offset)
    }

    /**
     * Returns the position from which the next token will be consumed
     */
    val offset: Long
        get() = tokenStream.offset

    override fun toString(): String {
        return "$tokenStream: [$start, $end]"
    }

    fun notifyFormat(marker: Any) {
        listener?.onFormat(marker, this)
    }

    interface Listener {
        fun onFormat(marker: Any, parserStream: ParserStream)
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
            return ParserStream(TokenStream.fromString(seq))
        }
    }
}
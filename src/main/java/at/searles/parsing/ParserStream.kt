package at.searles.parsing

import at.searles.lexer.TokenStream
import at.searles.lexer.Tokenizer
import at.searles.lexer.utils.IntervalSet

class ParserStream(private val stream: TokenStream) {

    /**
     * Marks the start of the current parsed element.
     */
    var start: Long = stream.offset()

    /**
     * Marks the end of the current parsed element.
     */
    var end: Long = stream.offset()

    var listener: Listener? = null

    var isBacktrackAllowed: Boolean = true

    fun tokStream(): TokenStream {
        return stream
    }

    fun createTrace(): Trace {
        return ParserStreamTrace(this)
    }

    fun parseToken(tokenizer: Tokenizer, tokId: Int, exclusive: IntervalSet): CharSequence? {
        val frame = tokenizer.matchToken(stream, tokId, exclusive)

        if (frame != null) {
            start = frame.startPosition()
            end = frame.endPosition()
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

        if(!isBacktrackAllowed) {
            throw BacktrackNotAllowedException(failedParser, offset, this)
        }

        stream.setPositionTo(offset)
    }

    /**
     * Returns the position from which the next token will be consumed
     */
    val offset: Long
        get() = stream.offset()

    override fun toString(): String {
        return "$stream: [$start, $end]"
    }

    fun <C> notifyAnnotationBegin(annotation: C) {
        if (listener != null) {
            listener!!.annotationBegin(this, annotation)
        }
    }

    fun <C> notifyAnnotationEndSuccess(annotation: C) {
        if (listener != null) {
            listener!!.annotationEndSuccess(this, annotation)
        }
    }

    fun <C> notifyAnnotationEndFail(annotation: C) {
        if (listener != null) {
            listener!!.annotationEndFail(this, annotation)
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
        fun <C> annotationBegin(parserStream: ParserStream, annotation: C)
        fun <C> annotationEndFail(parserStream: ParserStream, annotation: C)
        fun <C> annotationEndSuccess(parserStream: ParserStream, annotation: C)
    }

    interface SimpleListener : Listener {
        override fun <C> annotationBegin(parserStream: ParserStream, annotation: C) {
            // ignore
        }

        override fun <C> annotationEndFail(parserStream: ParserStream, annotation: C) {
            // ignore fails.
        }

        override fun <C> annotationEndSuccess(parserStream: ParserStream, annotation: C) {
            annotate(parserStream, annotation)
        }

        fun <C> annotate(parserStream: ParserStream, annotation: C)
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
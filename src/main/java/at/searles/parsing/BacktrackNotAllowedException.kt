package at.searles.parsing

import at.searles.parsing.Recognizable.Then

class BacktrackNotAllowedException(val failedParser: Then, val requestedOffset: Long, source: ParserStream) : RuntimeException() {
    val unitBeforeErrorStart: Long = source.start
    val unitBeforeErrorEnd: Long = source.end
    val unexpectedTokenStart: Long = source.tokStream().frame.start
    val unexpectedTokenEnd: Long = source.tokStream().frame.end

    override fun toString(): String {
        return String.format("%s expected after %s at %d-%d", failedParser.right, failedParser.left, unexpectedTokenStart, unexpectedTokenEnd)
    }
}
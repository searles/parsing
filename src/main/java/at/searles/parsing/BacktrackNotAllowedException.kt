package at.searles.parsing

import at.searles.parsing.Recognizable.Then

class BacktrackNotAllowedException(val failedParser: Then, val requestedOffset: Long, source: ParserStream) : RuntimeException() {
    val beforeErrorTrace: Trace = source.toTrace()
    val unexpectedTokenTrace: Trace = source.tokStream().frame.toTrace()

    override fun toString(): String {
        return String.format("%s expected after %s at %d-%d", failedParser.right, failedParser.left, unexpectedTokenTrace)
    }
}
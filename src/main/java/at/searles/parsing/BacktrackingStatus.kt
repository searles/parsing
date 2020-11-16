package at.searles.parsing

import at.searles.parsing.Recognizable.Then

class BacktrackingStatus(val failedParser: Then, val requestedOffset: Long, source: ParserStream) {
    val beforeErrorTrace: Trace = source.createTrace()
    val unexpectedTokenTrace: Trace = source.tokenStream.frame.toTrace()

    override fun toString(): String {
        return "after $beforeErrorTrace, expected ${failedParser.right}"
    }
}
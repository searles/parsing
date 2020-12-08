package at.searles.parsing

class BacktrackingStatus(val failedParser: Any, val requestedOffset: Long, source: ParserStream) {
    val beforeErrorTrace: Trace = source.createTrace()
    val unexpectedTokenTrace: Trace = source.tokenStream.frame.toTrace()

    override fun toString(): String {
        return "after $beforeErrorTrace, expected $failedParser"
    }
}
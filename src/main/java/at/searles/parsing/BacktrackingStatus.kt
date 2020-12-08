package at.searles.parsing

class BacktrackingStatus(private val failedParser: Any, val requestedOffset: Long, source: ParserStream) {
    private val beforeErrorTrace: Trace = source.createTrace()
//    val unexpectedTokenTrace: Trace = source.tokenStream.frame.toTrace()
//    val unexpectedToken: Trace = source.tokenStream.frame.toString()

    override fun toString(): String {
        return "after $beforeErrorTrace, expected $failedParser"
    }
}
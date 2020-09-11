package at.searles.parsing

class BacktrackNotAllowedException(val trace: BacktrackingTrace) : RuntimeException() {
    override fun toString(): String {
        return String.format("%s expected after %s at %d-%d", trace.failedParser.right, trace.failedParser.left, trace.unexpectedTokenTrace)
    }
}
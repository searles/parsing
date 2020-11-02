package at.searles.parsing

class BacktrackNotAllowedException(val `status `: BacktrackingStatus) : RuntimeException() {
    override fun toString(): String {
        return String.format("%s expected after %s at %d-%d", `status `.failedParser.right, `status `.failedParser.left, `status `.unexpectedTokenTrace)
    }
}
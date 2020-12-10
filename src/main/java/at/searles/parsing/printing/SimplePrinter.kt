package at.searles.parsing.printing

/**
 * A print writer for concrete syntax trees.
 */
class SimplePrinter(private val outStream: OutStream): CstVisitor {

    override fun visitMarker(marker: Any) {}

    override fun visitToken(seq: CharSequence) {
        append(seq)
    }

    /**
     * Raw-print into the underlying outStream.
     */
    private fun append(sequence: CharSequence) {
        outStream.append(sequence)
    }
}
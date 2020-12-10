package at.searles.parsing.printing

interface CstVisitor {
    fun visitToken(seq: CharSequence)
    fun visitMarker(marker: Any)
}
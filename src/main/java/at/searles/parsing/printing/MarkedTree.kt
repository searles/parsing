package at.searles.parsing.printing

class MarkedTree(private val marker: Any) : ConcreteSyntaxTree {
    override fun accept(visitor: CstVisitor) {
        visitor.visitMarker(marker)
    }

    override fun toString(): String {
        return ""
    }
}

package at.searles.parsing.printing

class FormatTree(private val marker: Any) : ConcreteSyntaxTree {
    override fun accept(visitor: CstVisitor) {
        visitor.visitFormat(marker)
    }

    override fun toString(): String {
        return ""
    }
}

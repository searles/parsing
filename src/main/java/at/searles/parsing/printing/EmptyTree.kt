package at.searles.parsing.printing

object EmptyTree : ConcreteSyntaxTree {
    override fun consRight(right: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return right
    }

    override fun consLeft(left: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return left
    }

    override fun accept(visitor: CstVisitor) {}

    override fun toString(): String {
        return ""
    }
}
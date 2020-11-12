package at.searles.parsing.printing

class EmptyConcreteSyntaxTree : ConcreteSyntaxTree {
    override fun consRight(right: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return right
    }

    override fun consLeft(left: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return left
    }

    override fun printTo(printer: CstPrinter) {}
    override fun toString(): String {
        return ""
    }
}
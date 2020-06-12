package at.searles.parsing.printing

class PartialConcreteSyntaxTree<L>(val left: L, val right: ConcreteSyntaxTree) {
    override fun toString(): String {
        return String.format("<%s, %s>", left, right)
    }

}
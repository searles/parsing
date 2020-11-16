package at.searles.parsing.printing

class PartialTree<L>(val left: L, val right: ConcreteSyntaxTree) {
    override fun toString(): String {
        return String.format("<%s, %s>", left, right)
    }
}
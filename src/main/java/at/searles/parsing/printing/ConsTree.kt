package at.searles.parsing.printing

class ConsTree(private val left: ConcreteSyntaxTree, private val right: ConcreteSyntaxTree) : ConcreteSyntaxTree {
    override fun toString(): String {
        return left.toString() + right.toString()
    }

    override fun accept(visitor: CstVisitor) {
        left.accept(visitor)
        var tree = right
        while (tree is ConsTree) {
            tree.left.accept(visitor)
            tree = tree.right
        }
        tree.accept(visitor)
    }

}
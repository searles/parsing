package at.searles.parsing.printer

class ComposedTree private constructor(private val leftTree: PrintTree, private val rightTree: PrintTree): PrintTree {
    override fun print(outStream: OutStream) {
        leftTree.print(outStream)

        var tree: PrintTree = rightTree

        while(tree is ComposedTree) {
            tree.leftTree.print(outStream)
            tree = tree.rightTree
        }

        tree.print(outStream)
    }

    override operator fun plus(right: PrintTree): PrintTree {
        return ComposedTree(leftTree, ComposedTree(rightTree, right))
    }

    override fun toString(): String {
        return asString()
    }

    companion object {
        fun of(left: PrintTree, right: PrintTree): PrintTree {
            return ComposedTree(left, right)
        }
    }
}

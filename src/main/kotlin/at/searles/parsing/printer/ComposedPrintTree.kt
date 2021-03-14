package at.searles.parsing.printer

class ComposedPrintTree private constructor(private val leftTree: PrintTree, private val rightTree: PrintTree): PrintTree {

    override fun print(outStream: OutStream) {
        leftTree.print(outStream)

        var tree: PrintTree = rightTree

        while(tree is ComposedPrintTree) {
            tree.leftTree.print(outStream)
            tree = tree.rightTree
        }

        tree.print(outStream)
    }

    override operator fun plus(right: PrintTree): PrintTree {
        return create(leftTree, ComposedPrintTree(rightTree, right))
    }

    override fun toString(): String {
        return asString()
    }

    companion object {
        fun create(left: PrintTree, right: PrintTree): PrintTree {
            require(left !is ComposedPrintTree)

            if(left == PrintTree.Empty) return right
            if(right == PrintTree.Empty) return left
            return ComposedPrintTree(left, right)
        }
    }
}

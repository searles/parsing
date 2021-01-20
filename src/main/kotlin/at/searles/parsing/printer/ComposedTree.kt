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

    override fun toString(): String {
        return asString()
    }

    companion object {
//
        fun of(left: PrintTree, right: PrintTree): PrintTree {
  /*          val list = left.toFlatList() + right.toFlatList()

            if(list.isEmpty()) {
                return PrintTree.Empty
            }

            if(list.size == 1) {
                return list.first()
            }
*/
            return ComposedTree(left, right)
        }
    }
}

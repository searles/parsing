package at.searles.parsing.printer

class ComposedTree private constructor(private val trees: List<PrintTree>): PrintTree {
    override fun print(outStream: OutStream) {
        for(tree in trees) {
            tree.print(outStream)
        }
    }

    override fun toString(): String {
        return trees.joinToString("")
    }

    companion object {
        private fun PrintTree.toFlatList(): List<PrintTree> {
            if(this == PrintTree.Empty) {
                return emptyList()
            }

            if(this is ComposedTree) {
                return trees
            }

            return listOf(this)
        }

        fun of(left: PrintTree, right: PrintTree): PrintTree {
            val list = left.toFlatList() + right.toFlatList()

            if(list.isEmpty()) {
                return PrintTree.Empty
            }

            if(list.size == 1) {
                return list.first()
            }

            return ComposedTree(list)
        }
    }
}

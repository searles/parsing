package at.searles.parsing.printer

class ComposedTree(private val trees: List<PrintTree>): PrintTree {
    override fun print(outStream: OutStream) {
        for(tree in trees) {
            tree.print(outStream)
        }
    }

    override operator fun plus(right: PrintTree): PrintTree {
        return ComposedTree(trees + right)
    }
}

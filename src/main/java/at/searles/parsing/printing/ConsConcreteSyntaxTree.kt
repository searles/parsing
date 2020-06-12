package at.searles.parsing.printing

class ConsConcreteSyntaxTree(private val left: ConcreteSyntaxTree, private val right: ConcreteSyntaxTree) : ConcreteSyntaxTree {
    override fun toString(): String {
        return left.toString() + right.toString()
    }

    override fun printTo(printer: CstPrinter) {
        left.printTo(printer)
        var tree = right
        while (tree is ConsConcreteSyntaxTree) {
            tree.left.printTo(printer)
            tree = tree.right
        }
        tree.printTo(printer)
    }

}
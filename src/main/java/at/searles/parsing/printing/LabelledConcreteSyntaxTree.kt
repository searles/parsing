package at.searles.parsing.printing

class LabelledConcreteSyntaxTree(private val label: String, private val child: ConcreteSyntaxTree) : ConcreteSyntaxTree {
    override fun toString(): String {
        return child.toString()
    }

    override fun printTo(printer: CstPrinter) {
        printer.print(child, label)
    }

}
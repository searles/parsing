package at.searles.parsing.printing

class AnnotatedConcreteSyntaxTree<C>(private val annotation: C, private val child: ConcreteSyntaxTree) : ConcreteSyntaxTree {
    override fun toString(): String {
        return child.toString()
    }

    override fun printTo(printer: CstPrinter) {
        printer.print(child, annotation)
    }

}
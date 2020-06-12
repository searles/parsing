package at.searles.parsing.printing

class LeafConcreteSyntaxTree(private val seq: CharSequence) : ConcreteSyntaxTree {
    override fun toString(): String {
        return seq.toString()
    }

    override fun printTo(printer: CstPrinter) {
        printer.print(seq)
    }

}
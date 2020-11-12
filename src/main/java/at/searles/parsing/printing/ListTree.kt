package at.searles.parsing.printing

class ListConcreteSyntaxTree(private val list: List<ConcreteSyntaxTree?>) : ConcreteSyntaxTree {
    override fun printTo(printer: CstPrinter) {
        for (tree in list) {
            tree!!.printTo(printer)
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        
        for (cst in list) {
            stringBuilder.append(cst)
        }

        return stringBuilder.toString()
    }
}
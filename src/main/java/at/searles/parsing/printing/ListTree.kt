package at.searles.parsing.printing

class ListTree(private val list: List<ConcreteSyntaxTree>) : ConcreteSyntaxTree {
    override fun accept(visitor: CstVisitor) {
        list.forEach {
            it.accept(visitor)
        }
    }

    override fun toString(): String {
        return list.joinToString("")
    }
}
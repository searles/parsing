package at.searles.parsing.printing

class TokenTree(private val seq: CharSequence) : ConcreteSyntaxTree {
    override fun toString(): String {
        return seq.toString()
    }

    override fun accept(visitor: CstVisitor) {
        visitor.visitToken(seq)
    }

}
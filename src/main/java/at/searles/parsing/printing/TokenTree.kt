package at.searles.parsing.printing

class TokenTree(seq: CharSequence) : ConcreteSyntaxTree {
    private val token = seq.toString()

    override fun toString(): String {
        return token
    }

    override fun accept(visitor: CstVisitor) {
        visitor.visitToken(token)
    }
}
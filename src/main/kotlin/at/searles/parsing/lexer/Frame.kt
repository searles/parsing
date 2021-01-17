package at.searles.parsing.lexer

interface Frame: CharSequence {
    val index: Long
    override val length: Int
}
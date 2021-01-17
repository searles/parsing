package at.searles.parsing.lexer

interface Frame {
    val index: Long
    val length: Long
    val string: String
}
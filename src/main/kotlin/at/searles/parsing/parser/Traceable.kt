package at.searles.parsing.parser

interface Traceable {
    fun setTrace(startIndex: Long, endIndex: Long)
}
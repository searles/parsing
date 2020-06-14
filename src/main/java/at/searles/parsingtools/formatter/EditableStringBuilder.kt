package at.searles.parsingtools.formatter

import java.lang.StringBuilder

class EditableStringBuilder(val sb: StringBuilder): EditableText {
    override fun delete(start: Long, end: Long) {
        sb.delete(start.toInt(), end.toInt())
    }

    override fun insert(position: Long, insertion: CharSequence) {
        sb.insert(position.toInt(), insertion.toString())
    }

    override val length: Int
        get() = sb.length

    override fun get(index: Int): Char {
        return sb[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return sb.subSequence(startIndex, endIndex)
    }
}
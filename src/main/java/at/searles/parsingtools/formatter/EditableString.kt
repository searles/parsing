package at.searles.parsingtools.formatter

import java.lang.StringBuilder

class EditableStringBuilder(private val sb: StringBuilder = StringBuilder()): EditableText {

    override val length: Int
        get() = sb.length

    override fun delete(position: Long, length: Long) {
        sb.delete(position.toInt(), (position + length).toInt())
    }

    override fun insert(position: Long, insertion: CharSequence) {
        sb.insert(position.toInt(), insertion.toString())
    }

    override fun get(index: Int): Char {
        return sb[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return sb.subSequence(startIndex, endIndex)
    }
}
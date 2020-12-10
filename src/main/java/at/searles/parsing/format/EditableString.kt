package at.searles.parsing.format

import java.lang.StringBuilder

class EditableString(string: String): EditableText {

    private val sb = StringBuilder(string)

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

    override fun toString(): String {
        return sb.toString()
    }
}
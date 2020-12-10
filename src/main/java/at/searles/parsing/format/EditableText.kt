package at.searles.parsing.format

interface EditableText: CharSequence {
    fun insert(position: Long, insertion: CharSequence)
    fun delete(position: Long, length: Long)
}
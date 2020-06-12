package at.searles.parsing

interface Consumer<T> {
    fun consume(stream: ParserStream, t: T): Boolean
    fun inverse(): T? {
        return null
    }
}
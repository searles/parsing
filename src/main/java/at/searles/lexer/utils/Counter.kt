package at.searles.lexer.utils

class Counter: Iterator<Int> {
    private var i = 0
    fun incr(): Int {
        return i++
    }

    fun get(): Int {
        return i
    }

    override fun hasNext(): Boolean {
        return true
    }

    override fun next(): Int {
        return incr()
    }
}
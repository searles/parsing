package at.searles.parsing

interface Fold<T, U, V> {
    fun apply(stream: ParserStream, left: T, right: U): V

    fun leftInverse(result: V): T? {
        return null
    }

    fun rightInverse(result: V): U? {
        return null
    }

    companion object {
        fun <T, U, V> create(fn: (T, U) -> V): Fold<T, U, V> {
            return object: Fold<T, U, V> {
                override fun apply(stream: ParserStream, left: T, right: U): V {
                    return fn(left, right)
                }
            }
        }
    }
}
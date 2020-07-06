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

        fun <T, U, V> create(leftFn: (V) -> T?, rightFn: (V) -> U?, fn: (T, U) -> V): Fold<T, U, V> {
            return object: Fold<T, U, V> {
                override fun apply(stream: ParserStream, left: T, right: U): V {
                    return fn(left, right)
                }

                override fun leftInverse(result: V): T? {
                    return leftFn(result)
                }

                override fun rightInverse(result: V): U? {
                    return rightFn(result)
                }
            }
        }

        fun <T, U, V> create(leftFn: (V) -> T?, rightFn: (V) -> U?, fn: (Trace, T, U) -> V): Fold<T, U, V> {
            return object: Fold<T, U, V> {
                override fun apply(stream: ParserStream, left: T, right: U): V {
                    return fn(stream.toTrace(), left, right)
                }

                override fun leftInverse(result: V): T? {
                    return leftFn(result)
                }

                override fun rightInverse(result: V): U? {
                    return rightFn(result)
                }
            }
        }
    }
}
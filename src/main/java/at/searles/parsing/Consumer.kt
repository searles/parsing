package at.searles.parsing

interface Consumer<T> {
    fun consume(stream: ParserStream, t: T): Boolean
    fun inverse(): T? {
        return null
    }

    companion object {
        fun <T>create(consumer: (T) -> Boolean): Consumer<T> {
            return object: Consumer<T> {
                override fun consume(stream: ParserStream, t: T): Boolean {
                    return consumer(t)
                }
            }
        }

        fun <T>create(inverted: () -> T?, consumer: (T) -> Boolean): Consumer<T> {
            return object: Consumer<T> {
                override fun consume(stream: ParserStream, t: T): Boolean {
                    return consumer(t)
                }

                override fun inverse(): T? {
                    return inverted()
                }
            }
        }

        fun <T>create(inverted: () -> T?, consumer: (Trace, T) -> Boolean): Consumer<T> {
            return object: Consumer<T> {
                override fun consume(stream: ParserStream, t: T): Boolean {
                    return consumer(stream.createTrace(), t)
                }

                override fun inverse(): T? {
                    return inverted()
                }
            }
        }
    }
}
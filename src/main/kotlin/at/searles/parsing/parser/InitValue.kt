package at.searles.parsing.parser

class InitValue<A>(value: () -> A): Initializer<A> {

    private val value = value()

    override fun initialize(): A {
        return value
    }

    override fun consume(value: A): Boolean {
        return this.value == value
    }
}
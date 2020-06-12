package at.searles.parsing

import at.searles.parsing.printing.ConcreteSyntaxTree

class Ref<T>(private val label: String) : Parser<T> {
    private var ref: Parser<T>? = null
    fun set(ref: Parser<T>?): Ref<T> {
        this.ref = ref
        return this
    }

    override fun recognize(stream: ParserStream): Boolean {
        return try {
            ref!!.recognize(stream)
        } catch (e: StackOverflowError) {
            throw PossiblyInfiniteRecursionException(this, e)
        }
    }

    override fun parse(stream: ParserStream): T? {
        return try {
            ref!!.parse(stream)
        } catch (e: StackOverflowError) {
            throw PossiblyInfiniteRecursionException(this, e)
        }
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return try {
            ref!!.print(item)
        } catch (e: StackOverflowError) {
            throw PossiblyInfiniteRecursionException(this, e)
        }
    }

    override fun toString(): String {
        return label
    }

}
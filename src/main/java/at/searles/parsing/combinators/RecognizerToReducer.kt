package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

class RecognizerToReducer<T>(private val recognizer: Recognizer) : Reducer<T, T> {
    override fun reduce(left: T, stream: ParserStream): T? {
        if(!stream.recognize(recognizer, false)) {
            return null
        }

        return left
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(recognizer, false)
    }

    override fun print(item: T): PartialTree<T> {
        return PartialTree(item, recognizer.print())
    }

    override fun toString(): String {
        return recognizer.toString()
    }
}

package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

class RecognizerToReducer<T>(private val recognizer: Recognizer) : Reducer<T, T> {
    override fun parse(stream: ParserStream, input: T): T? {
        val preStart = stream.start

        if(recognizer.recognize(stream)) {
            stream.start = preStart
            return input
        }

        return null
    }

    override fun recognize(stream: ParserStream): Boolean {
        val preStart = stream.start

        if(recognizer.recognize(stream)) {
            stream.start = preStart
            return true
        }

        return false
    }

    override fun print(item: T): PartialTree<T>? {
        return PartialTree(item, recognizer.print())
    }

    override fun toString(): String {
        return recognizer.toString()
    }
}

package at.searles.parsing.label

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Using this parser we obtain recursion.
 */
class RecognizerRef(private val label: String) : Recognizer {

    lateinit var ref: Recognizer

    override fun recognize(stream: ParserStream): Boolean {
        stream.fireRefStart(label)
        return ref.recognize(stream).also {
            if(it) {
                stream.fireRefSuccess(label)
            } else {
                stream.fireRefFail(label)
            }
        }
    }

    override fun print(): ConcreteSyntaxTree {
        return ref.print().ref(label)
    }

    override fun toString(): String {
        return label
    }

}
package at.searles.parsing.annotation

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Using this parser we obtain recursion.
 */
class AnnotationRecognizer<A>(private val annotation: A, private val recognizer: Recognizer) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        stream.notifyAnnotationBegin(annotation)
        return recognizer.recognize(stream).also {
            if(it) {
                stream.notifyAnnotationEndSuccess(annotation)
            } else {
                stream.notifyAnnotationEndFail(annotation)
            }
        }
    }

    override fun print(): ConcreteSyntaxTree {
        return recognizer.print().annotate(annotation)
    }

    override fun toString(): String {
        return recognizer.toString()
    }

}
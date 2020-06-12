package at.searles.parsing.annotation

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

class AnnotationParser<C, T>(private val annotation: C, private val parser: Parser<T>) : Parser<T> {
    override fun parse(stream: ParserStream): T? {
        stream.notifyAnnotationBegin(annotation)
        return parser.parse(stream).also {
            if(it != null) {
                stream.notifyAnnotationEndSuccess(annotation)
            } else {
                stream.notifyAnnotationEndFail(annotation)
            }
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.notifyAnnotationBegin(annotation)
        return if(parser.recognize(stream)) {
            stream.notifyAnnotationEndSuccess(annotation)
            true
        } else {
            stream.notifyAnnotationEndFail(annotation)
            false
        }
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return parser.print(item)?.annotate(annotation)
    }

    override fun toString(): String {
        return parser.toString()
    }

}
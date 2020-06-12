package at.searles.parsing.annotation

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

class AnnotationReducer<C, U, T>(private val annotation: C, private val reducer: Reducer<T, U>) : Reducer<T, U> {
    override fun parse(stream: ParserStream, input: T): U? {
        stream.notifyAnnotationBegin(annotation)
        return reducer.parse(stream, input).also {
            if(it != null) {
                stream.notifyAnnotationEndSuccess(annotation)
            } else {
                stream.notifyAnnotationEndFail(annotation)
            }
        }
    }

    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        val tree = reducer.print(item) ?: return null
        return PartialConcreteSyntaxTree(tree.left, tree.right.annotate(annotation))
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.notifyAnnotationBegin(annotation)
        return reducer.recognize(stream).also {
            if(it) {
                stream.notifyAnnotationEndSuccess(annotation)
            } else {
                stream.notifyAnnotationEndFail(annotation)
            }
        }
    }

    override fun toString(): String {
        return reducer.toString()
    }

}
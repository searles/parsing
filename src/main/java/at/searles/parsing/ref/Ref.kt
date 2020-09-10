package at.searles.parsing.ref

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

class Ref<T>(private val label: String) : Parser<T> {

    lateinit var ref: Parser<T>

    override fun parse(stream: ParserStream): T? {
        stream.fireRefStart(label)
        return ref.parse(stream).also {
            if(it != null) {
                stream.fireRefSuccess(label)
            } else {
                stream.fireRefFail(label)
            }
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.fireRefStart(label)
        return if(ref.recognize(stream)) {
            stream.fireRefSuccess(label)
            true
        } else {
            stream.fireRefFail(label)
            false
        }
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return ref.print(item)?.annotate(label)
    }

    override fun toString(): String {
        return label
    }

}
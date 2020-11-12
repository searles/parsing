package at.searles.parsing.ref

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

class ReducerRef<T, U>(private val label: String) : Reducer<T, U> {

    lateinit var ref: Reducer<T, U>

    override fun parse(stream: ParserStream, input: T): U? {
        stream.fireRefStart(label)
        return ref.parse(stream, input).also {
            if(it != null) {
                stream.fireRefSuccess(label)
            } else {
                stream.fireRefFail(label)
            }
        }
    }

    override fun print(item: U): PartialTree<T>? {
        val tree = ref.print(item) ?: return null
        return PartialTree(tree.left, tree.right.ref(label))
    }

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

    override fun toString(): String {
        return label
    }

}
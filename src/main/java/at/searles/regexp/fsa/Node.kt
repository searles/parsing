package at.searles.regexp.fsa

import at.searles.lexer.utils.IntervalSet

class Node {
    var connections = IntervalMap<Node>()
    var isFinal: Boolean = false

    private val label = "t${counter++}"

    fun connectTo(dst: Node, intervals: IntervalSet) {
        intervals.forEach {
            connections.add(it, dst) { _, _ ->
                error("Overlap with existing connection")
            }
        }
    }

    override fun toString(): String {
        return label + if(isFinal) "*" else ""
    }

    companion object {
        private var counter = 0
    }
}
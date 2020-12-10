package at.searles.regexp.fsa

import at.searles.lexer.utils.IntSet
import at.searles.lexer.utils.IntervalSet

open class Node(var isFinal: Boolean = false, set: IntSet = IntSet()) {

    val set = IntSet().apply {
        this.addAll(set)
    }

    var connections = IntervalMap<Node>()

    fun addId(id: Int) {
        set.add(id)
    }

    fun setPropertiesFromSet(nodes: Set<Node>) {
        isFinal = nodes.any {it.isFinal }
        nodes.forEach { set.addAll(it.set) }
    }

    fun accept(value: Int): Node? = connections[value]

    fun connectTo(dst: Node, intervals: IntervalSet) {
        intervals.forEach {
            connections.add(it, dst) { _, _ ->
                error("Overlap with existing connection")
            }
        }
    }

}
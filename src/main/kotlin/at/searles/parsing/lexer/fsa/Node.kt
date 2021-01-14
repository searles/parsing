package at.searles.parsing.lexer.fsa

open class Node(var isFinal: Boolean = false, set: IntSet = IntSet()) {

    val acceptedIds = IntSet().apply {
        this.addAll(set)
    }

    var edges = IntervalMap<Node>()

    fun addId(id: Int) {
        acceptedIds.add(id)
    }

//    fun setPropertiesFromSet(nodes: Set<Node>) {
//        isFinal = nodes.any {it.isFinal }
//        nodes.forEach { acceptedIds.addAll(it.acceptedIds) }
//    }

    operator fun get(value: Int): Node? = edges[value]

    fun connectTo(dst: Node, intervals: IntervalSet) {
        intervals.forEach {
            edges.add(it, dst) { _, _ ->
                error("Overlap with existing connection")
            }
        }
    }

}
package at.searles.parsing.lexer.fsa

import at.searles.parsing.lexer.FrameStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Automaton(val startNode: Node = Node()) {

    fun accept(stream: FrameStream): Node? {
        var n: Node? = startNode
        var acceptedNode: Node? = null

        while (n != null) {
            if (n.isFinal) {
                stream.setFrameEnd()
                acceptedNode = n
            }

            val ch = stream.read()
            n = n[ch]
        }

        return acceptedNode
    }

    fun union(other: Automaton): Automaton {
        require(this != other) { "Automata must be distinct" }

        val algorithm = DeterminizationAlgorithm()

        algorithm.epsilonConnect(startNode, other.startNode)
        algorithm.addFinalStates(nodes.filter { it.isFinal } + other.nodes.filter { it.isFinal })

        return algorithm.createAutomaton(startNode)
    }

    fun concat(right: Automaton): Automaton {
        require(this != right) { "Automata must be distinct" }

        val algorithm = DeterminizationAlgorithm()

        nodes.filter { it.isFinal }.forEach {
            algorithm.epsilonConnect(it, right.startNode)
        }

        algorithm.addFinalStates(right.nodes.filter { it.isFinal })

        return algorithm.createAutomaton(startNode)
    }

    fun rep1(): Automaton {
        val algorithm = DeterminizationAlgorithm()

        nodes.filter { it.isFinal }.forEach {
            algorithm.epsilonConnect(it, startNode)
        }

        algorithm.addFinalStates(nodes.filter { it.isFinal })

        return algorithm.createAutomaton(startNode)
    }

    fun rep(): Automaton {
        val algorithm = DeterminizationAlgorithm()

        val newStartNode = Node()
        algorithm.addFinalStates(nodes.filter { it.isFinal } + newStartNode)

        algorithm.epsilonConnect(newStartNode, startNode)

        nodes.filter { it.isFinal }.forEach {
            algorithm.epsilonConnect(it, startNode)
        }


        return algorithm.createAutomaton(newStartNode)
    }

    fun opt(): Automaton {
        val algorithm = DeterminizationAlgorithm()

        val newStartNode = Node()
        algorithm.addFinalStates(nodes.filter { it.isFinal } + newStartNode)

        algorithm.epsilonConnect(newStartNode, startNode)
        return algorithm.createAutomaton(newStartNode)

    }

    fun intersect(other: Automaton): Automaton {
        val complement1 = this.createCopy().apply {
            makeComplement()
        }

        val complement2 = other.createCopy().apply {
            makeComplement()
        }

        val intersectComplement = complement1.union(complement2)

        return intersectComplement.apply {
            makeComplement()
            removeTraps()
        }
    }

    fun minus(other: Automaton): Automaton {
        val complement1 = this.createCopy().apply {
            makeComplement()
        }

        return complement1.union(other).apply {
            makeComplement()
            removeTraps()
        }
    }

    fun firstMatch(): Automaton {
        return createCopy().apply {
            nodes.forEach {
                if(it.isFinal) {
                    it.edges.clear()
                }
            }
        }
    }

    fun createCopy(): Automaton {
        val nodeMap = HashMap<Node, Node>()

        nodes.forEach {
            nodeMap[it] = Node(it.isFinal, it.acceptedIds)
        }

        nodes.forEach { node ->
            val copyNode = nodeMap.getValue(node)
            copyNode.edges = node.edges.mapValues { nodeMap.getValue(it) }
        }

        return Automaton(nodeMap.getValue(startNode))
    }

    private fun addTrap() {
        val trapNode = Node()

        trapNode.connectTo(trapNode, IntervalSet().apply { add(Interval.all) })

        nodes.forEach {
            it.edges.add(Interval.all, trapNode) { original, _ -> original }
        }
    }

    private fun removeTraps() {
        val reverseConnections = getReverseConnections()
        val reachableFromFinalStates = HashSet<Node>()

        nodes.filter { it.isFinal }.forEach {
            collectAllReachableFromState(it, reverseConnections, reachableFromFinalStates)
        }

        nodes.forEach { src ->
            src.edges.removeAll { !reachableFromFinalStates.contains(it.value) }
        }
    }

    private fun getReverseConnections(): Map<Node, Set<Node>> {
        val reverseConnections = HashMap<Node, HashSet<Node>>()
        nodes.forEach { src ->
            src.edges.values.forEach { dst ->
                reverseConnections.getOrPut(dst) { HashSet() }.add(src)
            }
        }

        return reverseConnections
    }

    private fun collectAllReachableFromState(node: Node, reverseConnections: Map<Node, Set<Node>>, reachableFromState: HashSet<Node>) {
        if(reachableFromState.contains(node)) {
            return
        }

        reachableFromState.add(node)

        reverseConnections[node]?.forEach {
            collectAllReachableFromState(it, reverseConnections, reachableFromState)
        }
    }

    private fun makeComplement() {
        addTrap()
        nodes.forEach {
            it.isFinal = !it.isFinal
        }
    }

    val nodes = object : Iterable<Node> {
        override fun iterator() = NodesIterator(startNode)
    }

    val finalNodes get() = nodes.filter { it.isFinal }

    fun setId(id: Int) {
        nodes.filter { it.isFinal }.forEach { it.addId(id) }
    }

    private class NodesIterator(node: Node): Iterator<Node> {
        val traversed = HashSet<Node>()
        val stack = Stack<Node>()

        init {
            stack.push(node)
        }

        override fun hasNext(): Boolean {
            return stack.isNotEmpty()
        }

        override fun next(): Node {
            val nextNode = stack.pop()

            nextNode.edges.values.forEach {
                if(!traversed.contains(it)) {
                    stack.push(it)
                    traversed.add(it)
                }
            }

            return nextNode
        }
    }

    override fun toString(): String {
        val labels = getLabels()

        return nodes.filter { !it.edges.isEmpty }.joinToString("; ") { src ->
            val srcLabel = labels[src]
            src.edges.joinToString(", ") {
                val dstLabel = labels[it.value]
                "$srcLabel --${it.interval}--> $dstLabel"
            }
        }
    }

    private fun getLabels(): Map<Node, String> {
        val labels = HashMap<Node, String>()

        var count = 0

        nodes.forEach {
            labels[it] = "q${count++}" + if(it.isFinal) "*" else ""
        }

        return labels
    }

    companion object {
        fun create(text: String): Automaton {
            val startNode = Node()

            var lastNode = startNode

            text.codePoints().forEach {
                val node = Node()
                lastNode.edges.add(Interval(it), node) { _, _ -> error("unexpected") }
                lastNode = node
            }

            lastNode.isFinal = true

            return Automaton(startNode)
        }

        fun create(set: IntervalSet): Automaton {
            val startNode = Node()
            val finalNode = Node()

            set.forEach {
                startNode.edges.add(it, finalNode) { _, _ -> error("unexpected") }
            }

            finalNode.isFinal = true

            return Automaton(startNode)
        }

        fun empty(): Automaton {
            val startNode = Node().apply {
                isFinal = true
            }

            return Automaton(startNode)
        }

        fun all(): Automaton {
            return create(IntervalSet(Interval(0, Int.MAX_VALUE)))
        }
    }
}
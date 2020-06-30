package at.searles.regexp.fsa

import at.searles.lexer.utils.Interval
import at.searles.lexer.utils.IntervalSet
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Automaton(val startNode: Node) {
    fun union(other: Automaton): Automaton {
        require(this != other) { "Automata must be distinct" }

        val algorithm = Determinization()

        algorithm.epsilonConnect(startNode, other.startNode)
        algorithm.addFinalStates(nodes.filter { it.isFinal } + other.nodes.filter { it.isFinal })

        return algorithm.createAutomaton(startNode)
    }

    fun concat(right: Automaton): Automaton {
        require(this != right) { "Automata must be distinct" }

        val algorithm = Determinization()

        nodes.filter { it.isFinal }.forEach {
            algorithm.epsilonConnect(it, right.startNode)
        }

        algorithm.addFinalStates(right.nodes.filter { it.isFinal })

        return algorithm.createAutomaton(startNode)
    }

    fun kleenePlus(): Automaton {
        val algorithm = Determinization()

        nodes.filter { it.isFinal }.forEach {
            algorithm.epsilonConnect(it, startNode)
        }

        algorithm.addFinalStates(nodes.filter { it.isFinal })

        return algorithm.createAutomaton(startNode)
    }

    fun kleeneStar(): Automaton {
        val algorithm = Determinization()

        val newStartNode = Node() // will be set final in Determinization
        algorithm.addFinalStates(nodes.filter { it.isFinal } + newStartNode)

        algorithm.epsilonConnect(newStartNode, startNode)

        nodes.filter { it.isFinal }.forEach {
            algorithm.epsilonConnect(it, startNode)
        }


        return algorithm.createAutomaton(newStartNode)
    }

    fun optional(): Automaton {
        val algorithm = Determinization()

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

        return complement1.union(complement2).apply {
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
                    it.connections.clear()
                }
            }
        }
    }

    fun createCopy(): Automaton {
        val nodeMap = HashMap<Node, Node>()

        nodes.forEach {
            nodeMap[it] = Node().apply {
                // TODO correct if sth is added
                isFinal = it.isFinal
            }
        }

        nodes.forEach { node ->
            val copyNode = nodeMap.getValue(node)
            // TODO Yuck, don't like.
            copyNode.connections = node.connections.mapValues { nodeMap.getValue(it) }
        }

        return Automaton(nodeMap.getValue(startNode))
    }

    private fun addTrap() {
        val trapNode = Node()

        trapNode.connectTo(trapNode, IntervalSet().apply { add(Interval.all) })

        nodes.forEach {
            it.connections.add(Interval.all, trapNode) { original, _ -> original }
        }
    }

    private fun removeTraps(node: Node = startNode, isTrapMap: HashMap<Node, Boolean> = HashMap()) {
        if(isTrapMap.contains(node)) {
            return
        }

        isTrapMap[node] = !node.isFinal // pessimistic

        node.connections.values.forEach {
            removeTraps(it, isTrapMap)
        }

        node.connections.removeAll {
            isTrapMap.getValue(it.value)
        }
    }

    private fun makeComplement() {
        addTrap()
        nodes.forEach {
            it.isFinal = !it.isFinal
        }
    }

    val nodes = object : Iterable<Node> {
        override fun iterator() = NodesIter(startNode)
    }

    private class NodesIter(node: Node): Iterator<Node> {
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

            nextNode.connections.values.forEach {
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

        return nodes.filter { !it.connections.isEmpty }.joinToString("; ") { src ->
            val srcLabel = labels[src]
            src.connections.joinToString(", ") {
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
                lastNode.connections.add(Interval(it), node) { _, _ -> error("unexpected") }
                lastNode = node
            }

            lastNode.isFinal = true

            return Automaton(startNode)
        }

        fun create(set: IntervalSet): Automaton {
            val startNode = Node()
            val finalNode = Node()

            set.forEach {
                startNode.connections.add(it, finalNode) { _, _ -> error("unexpected") }
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
    }
}
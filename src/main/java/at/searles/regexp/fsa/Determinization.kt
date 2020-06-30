package at.searles.regexp.fsa

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Determinization {
    private val table = HashMap<Set<Node>, IntervalMap<Set<Node>>>()
    private val nodeSetQueue = Stack<Set<Node>>()
    private val epsilonEdges = HashMap<Node, Node>()
    private val finalStates = HashSet<Node>()

    fun epsilonConnect(src: Node, dest: Node) {
        require(!epsilonEdges.contains(src))
        epsilonEdges[src] = dest
    }

    fun addFinalStates(nodes: Iterable<Node>) {
        finalStates.addAll(nodes)
    }

    fun createAutomaton(startNode: Node): Automaton {
        val startNodeSet = setOf(startNode)

        nodeSetQueue.push(startNodeSet)

        while(!nodeSetQueue.isEmpty()) {
            val nextNodeSet = nodeSetQueue.pop()
            addConnectionsToTable(nextNodeSet)
        }

        val nodesTable = createNodesTable() // createNodeSetToNewNodeTable()
        return Automaton(nodesTable.getValue(startNodeSet))
    }

    private fun createNodesTable(): Map<Set<Node>, Node> {
        val nodeTable = HashMap<Set<Node>, Node>()

        table.forEach { (nodeSet, connections) ->
            val node = getNodeFromSet(nodeSet, nodeTable)

            connections.forEach {
                node.connections.add(it.interval, getNodeFromSet(it.value, nodeTable)) { _, _ -> error("BUG!") }
            }
        }

        return nodeTable
    }

    private fun getNodeFromSet(nodeSet: Set<Node>, nodeTable: HashMap<Set<Node>, Node> = HashMap()): Node {
        val epsilonClosure = getEpsilonClosure(nodeSet)

        return nodeTable.getOrPut(nodeSet) {
            // TODO Create node with specific properties!
            Node().apply {
                isFinal = epsilonClosure.any { finalStates.contains(it) }
            }
        }
    }

    private fun getEpsilonClosure(set: Set<Node>): Set<Node> {
        val collected = HashSet<Node>()
        set.forEach {
            collectEpsilonConnectedNodes(it, collected)
        }
        return collected
    }

    private fun collectEpsilonConnectedNodes(node: Node, set: HashSet<Node>) {
        if(node in set) {
            return
        }

        set.add(node)

        epsilonEdges[node]?.also {
            collectEpsilonConnectedNodes(it, set)
        }
    }

    private fun addConnectionsToTable(nodeSet: Set<Node>) {
        if(table.contains(nodeSet)) {
            return
        }

        val connections = getConnections(nodeSet)

        connections.values.forEach {
            nodeSetQueue.push(it)
        }

        table[nodeSet] = connections
    }

    private fun getConnections(set: Set<Node>): IntervalMap<Set<Node>> {
        val epsilonClosure = getEpsilonClosure(set)

        val connections = IntervalMap<Set<Node>>()

        epsilonClosure.forEach {
            addToSetMap(connections, it.connections)
        }

        return connections
    }

    private fun addToSetMap(setMap: IntervalMap<Set<Node>>, newMap: IntervalMap<Node>) {
        newMap.forEach {
            setMap.add(it.interval, setOf(it.value)) { first, second -> first + second }
        }
    }
}

package at.searles.regexp

import at.searles.lexer.utils.Interval
import at.searles.lexer.utils.IntervalSet
import at.searles.regexp.fsa.Automaton
import at.searles.regexp.fsa.Node
import org.junit.Assert
import org.junit.Test

class AutomatonTest {
    @Test
    fun countNodesTest() {
        val node1 = Node()
        val node2 = Node()
        val node3 = Node()

        node1.connectTo(node2, IntervalSet(Interval(1, 2)))
        node1.connectTo(node3, IntervalSet(Interval(2, 3)))

        Assert.assertEquals(3, Automaton(node1).nodes.count())
    }

    @Test
    fun countConnectionsTest() {
        val node1 = Node()
        val node2 = Node()
        val node3 = Node()

        node1.connectTo(node2, IntervalSet(Interval(1, 2)))
        node1.connectTo(node3, IntervalSet(Interval(2, 3)))

        Assert.assertEquals(2, node1.connections.size)
    }

    @Test
    fun createCopyTest() {
        val node1 = Node()
        val node2 = Node()
        val node3 = Node()

        node1.connectTo(node2, IntervalSet(Interval(1, 2)))
        node1.connectTo(node3, IntervalSet(Interval(2, 3)))

        val automaton = Automaton(node1)

        Assert.assertEquals(automaton.toString(), automaton.createCopy().toString())
    }

}
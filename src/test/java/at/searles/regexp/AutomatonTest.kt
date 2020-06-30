package at.searles.regexp

import at.searles.buf.StringWrapper
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

    @Test
    fun acceptSingleLetter() {
        val automaton = Automaton.create("a")
        val stream = StringWrapper("a")

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals(-1, stream.next())
    }

    @Test
    fun acceptLetterInUnion() {
        val automaton = Automaton.create("a").union(Automaton.create("aa"))
        val stream = StringWrapper("a")

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals(-1, stream.next())
    }

    @Test
    fun acceptLettersInRep() {
        val automaton = Automaton.create("a").kleeneStar()
        val stream = StringWrapper("aaa")

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals(-1, stream.next())
    }

    @Test
    fun acceptLettersInRepWithNonFullMatch() {
        val automaton = Automaton.create("a").kleeneStar()
        val stream = StringWrapper("aaab")

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals("aaa", stream.frame().toString())
    }

    @Test
    fun firstMatchTest() {
        val automaton = Automaton.create("(").concat(Automaton.all().kleeneStar()).concat(Automaton.create(")")).firstMatch()
        val stream = StringWrapper("(aa))")

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals("(aa)", stream.frame().toString())
    }

    @Test
    fun longestMatchTest() {
        val automaton = Automaton.create("(").concat(Automaton.all().kleeneStar()).concat(Automaton.create(")"))
        val stream = StringWrapper("(aa))")

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals("(aa))", stream.frame().toString())
    }

    @Test
    fun mismatch() {
        val automaton = Automaton.create("ab")
        val stream = StringWrapper("aa")

        Assert.assertTrue(automaton.accept(stream) == null)
    }
}
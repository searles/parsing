package at.searles.parsing.lexer.fsa

import at.searles.parsing.codepoint.StringCodePointStream
import at.searles.parsing.lexer.FrameStream
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

        Assert.assertEquals(2, node1.edges.size)
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
        val stream = FrameStream(StringCodePointStream("a"))

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun acceptLetterInUnion() {
        val automaton = Automaton.create("a").union(Automaton.create("aa"))
        val stream = FrameStream(StringCodePointStream("a"))

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun acceptLettersInRep() {
        val automaton = Automaton.create("a").rep()
        val stream = FrameStream(StringCodePointStream("aaa"))

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun acceptLettersInRepWithNonFullMatch() {
        val automaton = Automaton.create("a").rep()
        val stream = FrameStream(StringCodePointStream("aaab"))

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals("aaa", stream.getFrame())
    }

    @Test
    fun firstMatchTest() {
        val automaton = Automaton.create("(").concat(Automaton.all().rep()).concat(Automaton.create(")")).firstMatch()
        val stream = FrameStream(StringCodePointStream("(aa))"))

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals("(aa)", stream.getFrame())
    }

    @Test
    fun longestMatchTest() {
        val automaton = Automaton.create("(").concat(Automaton.all().rep()).concat(Automaton.create(")"))
        val stream = FrameStream(StringCodePointStream("(aa))"))

        Assert.assertTrue(automaton.accept(stream) != null)
        Assert.assertEquals("(aa))", stream.getFrame())
    }

    @Test
    fun mismatch() {
        val automaton = Automaton.create("ab")
        val stream = FrameStream(StringCodePointStream("aa"))

        Assert.assertTrue(automaton.accept(stream) == null)
    }
}
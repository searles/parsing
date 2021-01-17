package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.fsa.RegexpToFsaVisitor
import org.junit.Assert
import org.junit.Test

class RegexpText {
    @Test
    fun testSingleChar() {
        val regexp = Text("a")
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals("q0 --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testCharSet() {
        val regexp = CharSet.chars('a', 'c')
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*, " +
                        "q0 --99..99--> q1*", automaton.toString())
    }


    @Test
    fun testUnion() {
        val regexp = Text("a") or Text("b")
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals("q0 --97..97--> q2*, q0 --98..98--> q1*", automaton.toString())
    }

    @Test
    fun testConcatenation() {
        val regexp = Text("a") + Text("b")
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1; " +
                        "q1 --98..98--> q2*", automaton.toString())
    }

    @Test
    fun testKleenePlus() {
        val regexp = Text("a").rep1()
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*; " +
                        "q1* --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testKleeneStar() {
        val regexp = Text("a").rep()
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0* --97..97--> q1*; " +
                        "q1* --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testOpt() {
        val regexp = Text("a").opt()
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0* --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testFirstMatch() {
        val regexp = Text("a").rep1().nonGreedy()
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testAnd() {
        val regexp = Text("a").or(Text("b")).and(Text("a"))
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testMinus() {
        val regexp = Text("a").or(Text("b")).minus(Text("b"))
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testMinusOfRep() {
        val regexp = Text("a").or(Text("b")).rep1().minus(Text("bb").rep())
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q3*, " +
                        "q0 --98..98--> q1*; " +
                        "q1* --97..97--> q3*, " +
                        "q1* --98..98--> q2; " +
                        "q2 --97..97--> q3*, " +
                        "q2 --98..98--> q1*; " +
                        "q3* --97..97--> q3*, " +
                        "q3* --98..98--> q4*; " +
                        "q4* --97..97--> q3*, " +
                        "q4* --98..98--> q4*", automaton.toString())
    }

    @Test
    fun testAndOfRep() {
        val regexp = Text("a").or(Text("b")).rep1().and(Text("bb").rep())
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --98..98--> q1; q1 --98..98--> q2*; q2* --98..98--> q1", automaton.toString())
    }

    @Test
    fun testAndOfSame() {
        val regexp = Text("a").or(Text("b")).rep1().and(Text("a").or(Text("b")).rep1())
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q2*, " +
                        "q0 --98..98--> q1*; " +
                        "q1* --97..97--> q2*, " +
                        "q1* --98..98--> q1*; " +
                        "q2* --97..97--> q2*, " +
                        "q2* --98..98--> q1*", automaton.toString())
    }

    @Test
    fun testAndOfSubset() {
        val regexp = Text("a").or(Text("aa")).and(Text("a"))
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*", automaton.toString())
    }

    @Test
    fun testAndOfSuperset() {
        val regexp = Text("a").or(Text("aa")).and(Text("a").rep())
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1*; q1* --97..97--> q2*", automaton.toString())
    }

    @Test
    fun testAndOfMultiplesOf2And3() {
        val regexp = Text("aa").rep().and(Text("aaa").rep())
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0* --97..97--> q1; " +
                "q1 --97..97--> q2; " +
                "q2 --97..97--> q3; " +
                "q3 --97..97--> q4; " +
                "q4 --97..97--> q5; " +
                "q5 --97..97--> q6*; " +
                "q6* --97..97--> q1", automaton.toString())
    }

    @Test
    fun testAndOfConcatenation() {
        val regexp = Text("a").or(Text("aa")).and(Text("aa"))
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals(
                "q0 --97..97--> q1; q1 --97..97--> q2*", automaton.toString())
    }

    @Test
    fun textEmptyIntersection() {
        val regexp = Text("a").and(Text("c"))
        val automaton = regexp.accept(RegexpToFsaVisitor)
        Assert.assertEquals("", automaton.toString())
    }
}
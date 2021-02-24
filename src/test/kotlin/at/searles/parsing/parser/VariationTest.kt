package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.parser.Parser.Companion.variation
import at.searles.parsing.parser.combinators.Variation
import at.searles.parsing.parser.tools.ListAppend
import at.searles.parsing.ruleset.Grammar
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VariationTest {
    lateinit var rules: Grammar
    lateinit var abcdVariation: Reducer<List<Int>, List<Int>>

    @Before
    fun setUp() {
        rules = object : Grammar {
            override val lexer = Lexer()
            val a = itext("a").init(1) + ListAppend()
            val b = itext("b").init(2) + ListAppend()
            val c = itext("c").init(3) + ListAppend()
            val d = itext("d").init(4) + ListAppend()

            val abcdVariation = Variation(listOf(a, b, c, d))
        }.also {
            this.abcdVariation = it.abcdVariation
        }
    }

    @Test
    fun testParseVariation() {
        val result = abcdVariation.parse(ParserStream("adb"), emptyList())
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(listOf(1, 4, 2), result.value)
        Assert.assertEquals(0, result.index)
        Assert.assertEquals(3, result.length)
    }

    @Test
    fun testPrintVariation() {
        val result = abcdVariation.print(listOf(4, 1, 2))
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("dab", result.rightTree.asString())
    }


    @Test
    fun testParseVariationWithoutRepetition() {
        val stream = ParserStream("abcda")
        val result = abcdVariation.parse(stream, emptyList())
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(4, stream.index)
    }

    @Test
    fun testPrintVariationWithoutRepetition() {
        val result = abcdVariation.print(listOf(4, 1, 2, 3, 4))
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(1, result.leftValue.size)
    }

    @Test
    fun testVariationParser() {
        val rules = object: Grammar {
            override val lexer: Lexer = Lexer()
            val a1 = itext("a").init(1)
            val b2 = itext("b").init(2)
            val c3 = itext("c").init(3)
            val d4 = itext("d").init(4)
            val parser = variation(a1, b2, c3, d4)
        }

        val result = rules.parser.parse(ParserStream("dbca"))
        Assert.assertEquals(listOf(4, 2, 3, 1), result.value)
    }
}
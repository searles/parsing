package at.searles.parsing.parser.format

import at.searles.parsing.lexer.fsa.IntSet
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.StringOutStream
import org.junit.Assert
import org.junit.Test
import java.lang.StringBuilder

class RulesTest {
    @Test
    fun testCanParseExpressions() {
        val result by Rules.expr.parse("f(1,2)")
        Assert.assertEquals(Call("f", listOf(Num("1"), Num("2"))), result)
    }

    @Test
    fun testParserIgnoresWs() {
        val result by Rules.expr.parse("f\n(\t1\r, 2)")
        Assert.assertEquals(Call("f", listOf(Num("1"), Num("2"))), result)
    }

    @Test
    fun testCanPrintExpression() {
        val result = Rules.expr.print(Call("f", listOf(Num("1"), Num("2"))))
        Assert.assertEquals("f(1,2)", result.asString())
    }

    @Test
    fun testCanParseEmptyBlocks() {
        val result by Rules.block.parse("{}")
        Assert.assertEquals(Block(emptyList()), result)
    }

    @Test
    fun testCanParseBlocksWithCall() {
        val result by Rules.block.parse("{f();}")
        Assert.assertEquals(Block(listOf(Call("f", emptyList()))), result)
    }

    @Test
    fun testCanPrintEmptyBlocks() {
        val result = Rules.block.print(Block(emptyList()))
        Assert.assertEquals("{}", result.asString())
    }
}
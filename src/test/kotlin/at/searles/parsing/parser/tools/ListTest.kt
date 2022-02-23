package at.searles.parsing.parser.tools

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.parser.Parser.Companion.orEmpty
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.tools.CastBuilders.castAll
import org.junit.Assert
import org.junit.Test

class ListTest {
    @Test
    fun testPushBack() {
        val list = BacktrackingList<Int>()

        val list2 = list + 1 + 2
        val list3 = list2 + 3

        Assert.assertEquals(listOf(1, 2, 3), list3)

        val list4 = list2 + 4

        Assert.assertEquals(listOf(1, 2, 4), list4)
    }

    @Test
    fun testEmptyList() {
        val emptyList = CreateEmptyList<Int>()
        val list = emptyList.initialize()

        Assert.assertTrue(list.isEmpty())
        Assert.assertTrue(emptyList.consume(emptyList()))
        Assert.assertFalse(emptyList.consume(listOf(1)))
    }

    @Test
    fun testListWithSeparator() {
        val lexer = Lexer()
        val a = TokenRecognizer.text("a", lexer).init("a")
        val list = a.join(TokenRecognizer.text(",", lexer))

        val parseResult = list.parse(TokenStream("a,a,a"))
        val printResult = list.print(listOf("a", "a"))

        Assert.assertTrue(parseResult.isSuccess)
        Assert.assertEquals(listOf("a", "a", "a"), parseResult.value)

        Assert.assertTrue(printResult.isSuccess)
        Assert.assertEquals("a,a", printResult.asString())
    }

    @Test
    fun testOrEmpty() {
        val lexer = Lexer()
        val a = TokenRecognizer.text("a", lexer).init("a")
        val plus = TokenRecognizer.text("+", lexer)

        val list = (plus + a.join(TokenRecognizer.text(",", lexer))).orEmpty()

        val parseResult = list.parse(TokenStream("+a,a,a"))
        val printResult = list.print(listOf("a", "a"))
        val printResultEmpty = list.print(emptyList())

        Assert.assertTrue(parseResult.isSuccess)
        Assert.assertEquals(listOf("a", "a", "a"), parseResult.value)

        Assert.assertTrue(printResult.isSuccess)
        Assert.assertEquals("+a,a", printResult.asString())

        Assert.assertTrue(printResultEmpty.isSuccess)
        Assert.assertEquals("", printResultEmpty.asString())
    }

    @Test
    fun listAppendWithMin() {
        val append = ListAppend<Int>(2)

        Assert.assertEquals(3, append.invertRight(listOf(1, 2, 3)).value)
        Assert.assertFalse(append.invertLeft(listOf(1, 2)).isSuccess)
        Assert.assertFalse(append.invertRight(listOf(1, 2)).isSuccess)
    }

    @Test
    fun testCastAll() {
        val cast = castAll<Number>().from<Int>()

        Assert.assertEquals(listOf(1, 2), cast.convert(listOf(1, 2)))
        Assert.assertFalse(cast.invert(listOf<Number>(1, 2, 3.4)).isSuccess)
        Assert.assertEquals(listOf(1, 2, 3), cast.invert(listOf<Number>(1, 2, 3)).value)
    }
}
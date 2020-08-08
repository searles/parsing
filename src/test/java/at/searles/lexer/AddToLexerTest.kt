package at.searles.lexer

import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Test

class AddToLexerTest {
    @Test
    fun testOverlappingItems() {
        val lexer = Lexer()
        val id1 = lexer.add(Text("a").min(1))
        val id2 = lexer.add(Text("a").min(2))
        val id3 = lexer.add(Text("a").min(3))

        Assert.assertNotEquals(id1, id2)
        Assert.assertNotEquals(id2, id3)
        Assert.assertNotEquals(id3, id1)
    }
}
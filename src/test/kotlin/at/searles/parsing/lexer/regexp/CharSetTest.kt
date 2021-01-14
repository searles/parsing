package at.searles.parsing.lexer.regexp

import org.junit.Assert
import org.junit.Test

class CharSetTest {
    @Test
    fun testIChar() {
        val charSet = CharSet.ichars('a', 'B')

        Assert.assertTrue(charSet.contains('A'.toInt()))
        Assert.assertTrue(charSet.contains('b'.toInt()))
        Assert.assertTrue(charSet.contains('a'.toInt()))
        Assert.assertTrue(charSet.contains('B'.toInt()))
        Assert.assertFalse(charSet.contains('.'.toInt()))
        Assert.assertFalse(charSet.contains('c'.toInt()))
    }
}
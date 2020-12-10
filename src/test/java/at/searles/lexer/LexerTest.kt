package at.searles.lexer

import at.searles.lexer.TokenStream.Companion.fromString
import at.searles.regexp.CharSet
import at.searles.regexp.CharSet.Companion.chars
import at.searles.regexp.Regexp
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Test

/**
 * Test for basic lexer functionality.
 */
class LexerTest {
    private var lexer: Lexer? = null
    private var token = 0
    private fun with(regexp: Regexp) {
        lexer = Lexer()
        token = lexer!!.add(regexp)
    }

    private fun testIfIsAccepted(string: String, expected: String?) {
        val stream = fromString(string)
        val tokIds = stream.getAcceptedTokens(lexer!!)
        Assert.assertEquals(expected != null, tokIds != null)
        Assert.assertEquals(expected != null, tokIds != null && tokIds.contains(token))
        Assert.assertEquals(expected, if (tokIds != null) stream.frame.toString() else null)
    }

    @Test
    fun testRange02() {
        with(chars('a').range(0, 2))
        testIfIsAccepted("b", "")
        testIfIsAccepted("a", "a")
        testIfIsAccepted("aa", "aa")
        testIfIsAccepted("aaa", "aa")
    }

    @Test
    fun testRange13() {
        with(chars('a').range(1, 3))
        testIfIsAccepted("b", null)
        testIfIsAccepted("a", "a")
        testIfIsAccepted("aa", "aa")
        testIfIsAccepted("aaa", "aaa")
        testIfIsAccepted("aaaa", "aaa")
    }

    @Test
    fun testMinusRex() {
        with(CharSet.Companion.interval('a', 'z').rep1() - Text.imany("AAB", "AB"))
        testIfIsAccepted("b", "b")
        testIfIsAccepted("a", "a")
        testIfIsAccepted("aa", "aa")
        testIfIsAccepted("ab", "a")
        testIfIsAccepted("aab", "aa")
        // XXX To avoid that aa matches, a token [a-z]+ should be added to the tokenizer
        testIfIsAccepted("aaca", "aaca")
    }

    @Test
    fun testRange23() {
        with(chars('a').range(2, 3))
        testIfIsAccepted("b", null)
        testIfIsAccepted("a", null)
        testIfIsAccepted("aa", "aa")
        testIfIsAccepted("aaa", "aaa")
        testIfIsAccepted("aaaa", "aaa")
    }

    @Test
    fun testRange24() {
        with(chars('a').range(2, 4))
        testIfIsAccepted("b", null)
        testIfIsAccepted("a", null)
        testIfIsAccepted("aa", "aa")
        testIfIsAccepted("aaa", "aaa")
        testIfIsAccepted("aaaa", "aaaa")
        testIfIsAccepted("aaaaa", "aaaa")
    }

    @Test
    fun testOr() {
        with(Text("ab").or(Text("ac")))
        testIfIsAccepted("ab", "ab")
        testIfIsAccepted("ac", "ac")
        testIfIsAccepted("bc", null)
        testIfIsAccepted("aa", null)
    }
}
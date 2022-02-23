package at.searles.parsing.ruleset

import at.searles.parsing.lexer.regexp.*
import at.searles.parsing.lexer.TokenStream
import org.junit.Assert
import org.junit.Test

class RegexpGrammarTest {
    @Test
    fun testUnion() {
        val src = "a|b"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Union)
    }

    @Test
    fun testDiff() {
        val src = "a-b"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Diff)
    }

    @Test
    fun testConcat() {
        val src = "ab"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Concat)
    }

    @Test
    fun testRepeat0() {
        val src = "a*"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Rep)
    }

    @Test
    fun testRepeat1() {
        val src = "a+"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Rep1)
    }

    @Test
    fun testChar() {
        val src = "a"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Text)
        Assert.assertEquals("a", (regexp.value as Text).string)
    }

    @Test
    fun testNormalEscaped() {
        val src = "\\("
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Text)
        Assert.assertEquals("(", (regexp.value as Text).string)
    }

    @Test
    fun testLfEscaped() {
        val src = "\\n"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is Text)
        Assert.assertEquals("\n", (regexp.value as Text).string)
    }

    @Test
    fun testCharSetSingleChars() {
        val src = "[ab]"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue('a' in (regexp.value as CharSet))
        Assert.assertTrue('b' in (regexp.value as CharSet))
    }

    @Test
    fun testCharSetRange() {
        val src = "[a-c]"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue('a' in (regexp.value as CharSet))
        Assert.assertTrue('b' in (regexp.value as CharSet))
        Assert.assertTrue('c' in (regexp.value as CharSet))
    }

    @Test
    fun testCharSetInverted() {
        val src = "[^a-c]"
        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue('a' !in (regexp.value as CharSet))
        Assert.assertTrue('b' !in (regexp.value as CharSet))
        Assert.assertTrue('c' !in (regexp.value as CharSet))
        Assert.assertTrue('d' in (regexp.value as CharSet))
    }

    @Test
    fun testCodePointInString() {
        val src = "[\uD83C\uDF09]"

        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue(0x1F309 in (regexp.value as CharSet))
        Assert.assertTrue(0xD83C !in (regexp.value as CharSet))
        Assert.assertTrue(0xDF09 !in (regexp.value as CharSet))
    }

    @Test
    fun testLongCodePointAsEncoding() {
        val src = "[\\U0001F309]"

        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue(0x1F309 in (regexp.value as CharSet))
        Assert.assertTrue(0xD83C !in (regexp.value as CharSet))
        Assert.assertTrue(0xDF09 !in (regexp.value as CharSet))
    }

    @Test
    fun testShortCodePointAsEncoding() {
        val src = "[\\u0309]"

        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue(0x0309 in (regexp.value as CharSet))
    }

    @Test
    fun testVeryShortCodePointAsEncoding() {
        val src = "[\\xff]"

        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue(0xff in (regexp.value as CharSet))
    }

    @Test
    fun testLfInCharSet() {
        val src = "[\\n]"

        val regexp = RegexpGrammar.union.parse(TokenStream(src))

        Assert.assertTrue(regexp.isSuccess)
        Assert.assertTrue(regexp.value is CharSet)
        Assert.assertTrue('\n'.toInt() in (regexp.value as CharSet))
    }
}
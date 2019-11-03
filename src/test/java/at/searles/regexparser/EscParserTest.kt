package at.searles.regexparser

import org.junit.Assert
import org.junit.Test

class EscParserTest {
    @Test
    fun test() {
        val str = "\"\\x0c\\x85\\u2028\\u2029\""
        val parsed = EscStringParser.fetch(CodePointStream(str))
        val unparsed = EscStringParser.unparse(parsed)
        Assert.assertEquals(str, unparsed)
    }

    @Test
    fun testToJavaString() {
        val str = "\"\\\\\\x0c\\x85\\u2028\\u2029\\U0001ffff\\\"\""
        val parsed = EscStringParser.fetch(CodePointStream(str))
        val unparsed = EscStringParser.toJavaString(parsed)
        Assert.assertEquals("\"\\\\\\u000c\\u0085\\u2028\\u2029\\ud83f\\udfff\\\"\"", unparsed)
    }
}
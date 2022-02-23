package at.searles.parsing.parser

import at.searles.parsing.codepoint.IndexedStream
import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.codepoint.FrameStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.TokenParser
import org.junit.Assert
import org.junit.Test

class StressTest {
    val lexer = Lexer()

    val one = Conversion<CharSequence, Long> { 1L }

    val adder = Fold<Long, Long, Long> { left, right -> left + right }

    val a = TokenParser(lexer.createToken(CharSet('a')), lexer, one)
    val aCount = (a + adder).rep()

    fun generateAStream(count: Long): CodePointStream {
        return object: CodePointStream {
            var i = 0L
            override fun read(): Int {
                if(i >= count) {
                    return -1
                }

                i++
                return 'a'.toInt()
            }
        }
    }

    @Test
    fun testParseLotsOfAs() {
        val length = Int.MAX_VALUE.toLong() - 1L

        // Int.MAX_VALUE.toLong() + 1L - 1:20 on macbook air m1 2020
        //                             - 1:40 in old version!

        val aStream = generateAStream(length)
        val stream = FrameStream(IndexedStream.of(aStream))

        val parserStream = TokenStream(stream)
        val result = aCount.parse(parserStream, 0)

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(length, result.value)
        Assert.assertEquals(0, result.startIndex)
        Assert.assertEquals(length, result.endIndex)
    }
}
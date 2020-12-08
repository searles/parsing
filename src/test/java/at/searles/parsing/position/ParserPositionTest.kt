package at.searles.parsing.position

import at.searles.lexer.Lexer
import at.searles.parsing.*
import at.searles.parsing.Parser.Companion.fromRegex
import at.searles.parsingtools.common.ToString
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Test

class ParserPositionTest {
    val tokenizer = Lexer()
    val a = fromRegex(Text("A"), tokenizer, ToString)
    val b = fromRegex(Text("B"), tokenizer, ToString)
    val z = Recognizer.fromString("Z", tokenizer)
    val fail: Reducer<String, String> = object: Reducer<String, String> {
        override fun parse(left: String, stream: ParserStream): String? {
            return null
        }

        override fun recognize(stream: ParserStream): Boolean {
            return false
        }

        override fun toString(): String {
            return "{fail}"
        }
    }

    val joiner: Fold<String, String, String> = Fold.create { left: String, right: String -> left + right }
    private var parser: Parser<String>? = null
    private var output: String? = null

    fun positionAssert(start: Int, end: Int): Mapping<String, String> {
        return object: Mapping <String, String> {
            override fun parse(left: String, stream: ParserStream): String {
                Assert.assertEquals(start.toLong(), stream.start)
                Assert.assertEquals(end.toLong(), stream.end)
                return left
            }
        }
    }

    fun positionInitAssert(start: Int, end: Int): Initializer<String> {
        return object: Initializer<String> {
            override fun parse(stream: ParserStream): String {
                Assert.assertEquals(start.toLong(), stream.start)
                Assert.assertEquals(end.toLong(), stream.end)
                return ""
            }
        }
    }

    @Test
    fun singleCharTest() {
        withParser(a.plus(positionAssert(0, 1)))
        actParse("A")
        Assert.assertEquals("A", output)
    }

    @Test
    fun sequenceTest() {
        withParser(a.plus(b.plus(positionAssert(1, 2)).plus(joiner).plus(positionAssert(0, 2))))
        actParse("AB")
        Assert.assertEquals("AB", output)
    }

    @Test
    fun backtrackingParserResetTest() {
        withParser(a + ((b + joiner + fail) or positionAssert(0, 1)))
        actParse("AB")
        Assert.assertEquals("A", output)
    }

    @Test
    fun backtrackingRecognizerResetTest() {
        withParser(z.plus(
                a.plus(fail)
                        .or(positionInitAssert(0, 1))
        ))
        actParse("ZB")
        Assert.assertEquals("", output)
    }

    @Test
    fun backtrackingParserThenRecognizerTest() {
        withParser(a.plus(
                z.plus(fail)
                        .or(positionAssert(0, 1))
        ))
        actParse("AB")
        Assert.assertEquals("A", output)
    }

    private fun withParser(parser: Parser<String>) {
        this.parser = parser
    }

    private fun actParse(str: String) {
        val parserStream: ParserStream = ParserStream.create(str)
        output = parser!!.parse(parserStream)
    }
}
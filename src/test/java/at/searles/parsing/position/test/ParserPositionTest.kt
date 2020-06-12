package at.searles.parsing.position.test

import at.searles.lexer.Lexer
import at.searles.parsing.*
import at.searles.parsing.Parser.Companion.fromRegex
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.regex.Regex
import org.junit.Assert
import org.junit.Test

class ParserPositionTest {
    val tokenizer = Lexer()
    val a = fromRegex(Regex.text("A"), tokenizer, false, ToString)
    val b = fromRegex(Regex.text("B"), tokenizer, false, ToString)
    val z = Recognizer.fromString("Z", tokenizer, false)
    val fail: Reducer<String, String> = object: Reducer<String, String> {
        override fun parse(stream: ParserStream, input: String): String? {
            return null
        }

        override fun recognize(stream: ParserStream): Boolean {
            return false
        }
    }

    val joiner: Fold<String, String, String> = Fold.create { left: String, right: String -> left + right }
    private var parser: Parser<String>? = null
    private var output: String? = null

    fun positionAssert(start: Int, end: Int): Mapping<String, String> {
        return object: Mapping <String, String> {
            override fun parse(stream: ParserStream, input: String): String {
                Assert.assertEquals(start.toLong(), stream.start)
                Assert.assertEquals(end.toLong(), stream.end)
                return input
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
        withParser(a.then(positionAssert(0, 1)))
        actParse("A")
        Assert.assertEquals("A", output)
    }

    @Test
    fun sequenceTest() {
        withParser(a.then(b.then(positionAssert(1, 2)).fold(joiner).then(positionAssert(0, 2))))
        actParse("AB")
        Assert.assertEquals("AB", output)
    }

    @Test
    fun backtrackingParserResetTest() {
        withParser(a.then(
                b.fold(joiner).then(fail).or(positionAssert(0, 1))
        ))
        actParse("AB")
        Assert.assertEquals("A", output)
    }

    @Test
    fun backtrackingRecognizerResetTest() {
        withParser(z.then(
                a.then(fail)
                        .or(positionInitAssert(0, 1))
        ))
        actParse("ZB")
        Assert.assertEquals("", output)
    }

    @Test
    fun backtrackingParserThenRecognizerTest() {
        withParser(a.then(
                z.then(fail)
                        .or(positionAssert(0, 1))
        ))
        actParse("AB")
        Assert.assertEquals("A", output)
    }

    private fun withParser(parser: Parser<String>) {
        this.parser = parser
    }

    private fun actParse(str: String) {
        val parserStream: ParserStream = str.createParserStream()
        output = parser!!.parse(parserStream)
    }

    companion object {
        private val ToString: Mapping<CharSequence, String> = object : Mapping<CharSequence, String> {
            override fun parse(stream: ParserStream, input: CharSequence): String {
                return input.toString()
            }

            override fun left(result: String): CharSequence? {
                return result
            }
        }
    }
}
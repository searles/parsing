package at.searles.parsing.recognizable

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.*
import at.searles.regex.RegexParser
import org.junit.Assert
import org.junit.Test

class RecognizableTest {
    private lateinit var parser: Parser<String>
    private lateinit var inputString: String

    private val lexer = SkipTokenizer(Lexer()).also {
        val ws = it.add(RegexParser.parse("[ \n]+"));
        it.addSkipped(ws)
    }

    private val id = Parser.fromRegex(RegexParser.parse("[a-z]+"), lexer, true, object: Mapping<CharSequence, String> {
        override fun parse(stream: ParserStream?, left: CharSequence): String? = left.toString()
        override fun left(result: String): CharSequence? = result
    })

    private val append: Fold<String, String, String> = Fold { _, l: String, r: String -> l + r }

    @Test
    fun thenTest() {
        withInput("a b")

        withParser(id.then(id.fold(append)))

        checkParseRecognizeEquality(true)
    }

    @Test
    fun optTest() {
        withInput("a b")

        withParser(id.then(Reducer.opt(id.fold(append))))

        checkParseRecognizeEquality(true)
    }

    @Test
    fun repTest() {
        withInput("a b")

        withParser(id.then(Reducer.rep(id.fold(append))))

        checkParseRecognizeEquality(true)
    }

    private fun withParser(parser: Parser<String>) {
        this.parser = parser
    }

    private fun checkParseRecognizeEquality(success: Boolean) {
        val stream1 = ParserStream.fromString(inputString)
        val stream2 = ParserStream.fromString(inputString)

        val success1 = parser.parse(stream1) != null
        val success2 = parser.recognize(stream2)

        Assert.assertEquals(success, success1)
        Assert.assertEquals(success, success2)
        Assert.assertEquals(stream1.getEnd(), stream2.getEnd())
    }

    private fun withInput(inputString: String) {
        this.inputString = inputString
    }
}
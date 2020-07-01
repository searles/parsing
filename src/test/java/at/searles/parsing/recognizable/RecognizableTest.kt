package at.searles.parsing.recognizable

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.*
import at.searles.parsing.Reducer.Companion.opt
import at.searles.parsing.Reducer.Companion.rep
import at.searles.regexparser.RegexpParser
import org.junit.Assert
import org.junit.Test

class RecognizableTest {
    private lateinit var parser: Parser<String>
    private lateinit var inputString: String

    private val lexer = SkipTokenizer(Lexer()).also {
        val ws = it.add(RegexpParser.parse("[ \n]+"))
        it.addSkipped(ws)
    }

    private val id = Parser.fromRegex(RegexpParser.parse("[a-z]+"), lexer, object: Mapping<CharSequence, String> {
        override fun parse(stream: ParserStream, input: CharSequence): String = input.toString()
        override fun left(result: String): CharSequence? = result
    }) // TODO intervalset needed?

    private val append: Fold<String, String, String> = Fold.create { l: String, r: String -> l + r }

    @Test
    fun thenTest() {
        withInput("a b")

        withParser(id.plus(id.fold(append)))

        checkParseRecognizeEquality(true)
    }

    @Test
    fun optTest() {
        withInput("a b")

        withParser(id.plus(id.fold(append).opt()))

        checkParseRecognizeEquality(true)
    }

    @Test
    fun repTest() {
        withInput("a b")

        withParser(id.plus(id.fold(append).rep()))

        checkParseRecognizeEquality(true)
    }

    private fun withParser(parser: Parser<String>) {
        this.parser = parser
    }

    private fun checkParseRecognizeEquality(success: Boolean) {
        val stream1 = ParserStream.create(inputString)
        val stream2 = ParserStream.create(inputString)

        val success1 = parser.parse(stream1) != null
        val success2 = parser.recognize(stream2)

        Assert.assertEquals(success, success1)
        Assert.assertEquals(success, success2)
        Assert.assertEquals(stream1.end, stream2.end)
    }

    private fun withInput(inputString: String) {
        this.inputString = inputString
    }
}
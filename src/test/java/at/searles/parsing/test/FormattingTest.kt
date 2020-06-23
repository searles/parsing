package at.searles.parsing.test

import at.searles.buf.Frame
import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.lexer.TokenStream
import at.searles.parsing.*
import at.searles.parsing.Parser.Companion.fromRegex
import at.searles.parsing.Reducer.Companion.rep
import at.searles.regexp.Regexp
import at.searles.regexparser.RegexpParser
import org.junit.Test

class FormattingTest {
    internal enum class Annotation {
        BLOCK, ARGUMENT
    }

    @Test
    fun test() {
        // XXX this test currently only checks whether everything works without problems
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)
        val ws = lexer.add(RegexpParser.parse("[ \n\r\t]+"))
        tokenizer.addSkipped(ws)
        val a = fromRegex(Regexp.text("a"), tokenizer, false, ToString)
        val open = Recognizer.fromString("(", tokenizer, false)
        val close = Recognizer.fromString(")", tokenizer, false)
        val expr = Ref<String>("expr")
        val term = a.or(open.plus(expr).annotate(Annotation.BLOCK).plus(close))
        expr.ref = term + term.annotate(Annotation.ARGUMENT).fold(Fold.create<String, String, String> { left, right -> left + right }).rep()
        val stream: ParserStream = ParserStream.create("a(aa((aaa)a)a)")
        stream.tokStream().setListener { _: TokenStream, _: Int, _: Frame -> }
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
package at.searles.parsing

import at.searles.buf.Frame
import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.lexer.TokenStream
import at.searles.parsing.Parser.Companion.fromRegex
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.ref.RefParser
import at.searles.regexp.Text
import at.searles.regexparser.RegexpParser
import org.junit.Test

class FormattingTest {
    internal object Annotation {
        const val BLOCK = "block"
        const val ARGUMENT = "argument"
    }

    @Test
    fun test() {
        // XXX this test currently only checks whether everything works without problems
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)
        val ws = tokenizer.addSkipped(RegexpParser.parse("[ \n\r\t]+"))
        val a = fromRegex(Text("a"), tokenizer, ToString)
        val open = Recognizer.fromString("(", tokenizer)
        val close = Recognizer.fromString(")", tokenizer)
        val expr = RefParser<String>("expr")
        val term = a.or(open.plus(expr).ref(Annotation.BLOCK).plus(close))
        expr.ref = term + term.ref(Annotation.ARGUMENT).plus(Fold.create<String, String, String> { left, right -> left + right }).rep()
        val stream: ParserStream = ParserStream.create("a(aa((aaa)a)a)")
        stream.tokenStream.listener = object: TokenStream.Listener {
            override fun tokenConsumed(src: TokenStream, tokenId: Int, frame: Frame) {}
        }
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
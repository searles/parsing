package at.searles.parsing.test;

import at.searles.buf.FrameStream;
import at.searles.lexer.LexerWithHidden;
import at.searles.lexer.TokStream;
import at.searles.parsing.*;
import at.searles.parsing.utils.common.ToString;
import at.searles.regex.Regex;
import at.searles.regex.RegexParser;
import org.junit.Test;

public class FormattingTest {

    enum Annotation { BLOCK, ARGUMENT };

    @Test
    public void test() {
        // XXX this test currently only checks whether everything works without problems
        LexerWithHidden lexer = new LexerWithHidden();

        int ws = lexer.addHiddenToken(RegexParser.parse("[ \n\r\t]+"));

        Parser<String> a = Parser.fromToken(lexer.token(Regex.text("a")), ToString.getInstance(), false);
        Recognizer open = Recognizer.fromString("(", lexer, false);
        Recognizer close = Recognizer.fromString(")", lexer, false);

        Ref<String> expr = new Ref<>("expr");

        Parser<String> term = a.or(open.then(expr).annotate(Annotation.BLOCK).then(close));
        expr.set(term.then(
                Reducer.rep(
                        term
                                .annotate(Annotation.ARGUMENT)
                                .fold((stream, left, right) -> left + right)
                )
        ));

        ParserStream stream = ParserStream.fromString("a(aa((aaa)a)a)");

        stream.tokStream().setListener((src, tokId, frame) -> {

        });
    }
}

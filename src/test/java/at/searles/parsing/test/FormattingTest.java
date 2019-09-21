package at.searles.parsing.test;

import at.searles.lexer.Lexer;
import at.searles.lexer.SkipTokenizer;
import at.searles.parsing.*;
import at.searles.parsing.utils.common.ToString;
import at.searles.regex.Regex;
import at.searles.regex.RegexParser;
import org.junit.Test;

public class FormattingTest {

    enum Annotation { BLOCK, ARGUMENT }

    @Test
    public void test() {
        // XXX this test currently only checks whether everything works without problems
        Lexer lexer = new Lexer();
        SkipTokenizer tokenizer = new SkipTokenizer(lexer);

        int ws = lexer.add(RegexParser.parse("[ \n\r\t]+"));
        tokenizer.addSkipped(ws);

        Parser<String> a = Parser.fromRegex(Regex.text("a"), tokenizer, false, ToString.getInstance());
        Recognizer open = Recognizer.fromString("(", tokenizer, false);
        Recognizer close = Recognizer.fromString(")", tokenizer, false);

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

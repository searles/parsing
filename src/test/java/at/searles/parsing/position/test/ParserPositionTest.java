package at.searles.parsing.position.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.regex.Regex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

public class ParserPositionTest {
    private static final Mapping<CharSequence, String> ToString = new Mapping<CharSequence, String>() {
        @Override
        public String parse(ParserStream stream, @NotNull CharSequence left) {
            return left.toString();
        }

        @Nullable
        @Override
        public CharSequence left(@NotNull String result) {
            return result;
        }
    };

    final Lexer tokenizer = new Lexer();
    final Parser<String> a = Parser.fromRegex(Regex.text("A"), tokenizer, false,  ToString);
    final Parser<String> b = Parser.fromRegex(Regex.text("B"), tokenizer, false, ToString);

    final Recognizer z = Recognizer.fromString("Z", tokenizer, false);

    final Mapping<String, String> fail = (stream, left) -> null;

    final Fold<String, String, String> joiner = (stream, left, right) -> left + right;
    private Parser<String> parser;
    private String output;

    Mapping<String, String> positionAssert(int start, int end) {
        return (stream, left) -> {
            Assert.assertEquals(start, stream.getStart());
            Assert.assertEquals(end, stream.getEnd());
            return left;
        };
    }

    Initializer<String> positionInitAssert(int start, int end) {
        return (stream) -> {
            Assert.assertEquals(start, stream.getStart());
            Assert.assertEquals(end, stream.getEnd());
            return "";
        };
    }

    @Test
    public void singleCharTest() {
        withParser(a.then(positionAssert(0, 1), true));
        actParse("A");
        Assert.assertEquals("A", output);
    }

    @Test
    public void sequenceTest() {
        withParser(a.then(b.then(positionAssert(1, 2)).fold(joiner).then(positionAssert(0, 2))));
        actParse("AB");
        Assert.assertEquals("AB", output);
    }

    @Test
    public void backtrackingParserResetTest() {
        withParser(a.then(
                b.fold(joiner).then(fail, true)
                        .or(positionAssert(0, 1))
        ));
        actParse("AB");
        Assert.assertEquals("A", output);
    }

    @Test
    public void backtrackingRecognizerResetTest() {
        withParser(z.then(
                a.then(fail)
                        .or(positionInitAssert(0, 1))
        ));
        actParse("ZB");
        Assert.assertEquals("", output);
    }

    @Test
    public void backtrackingParserThenRecognizerTest() {
        withParser(a.then(
                z.then(fail)
                        .or(positionAssert(0, 1))
        ));
        actParse("AB");
        Assert.assertEquals("A", output);
    }

    private void withParser(Parser<String> parser) {
        this.parser = parser;
    }

    private void actParse(String str) {
        ParserStream parserStream = ParserStream.fromString(str);
        output = parser.parse(parserStream);
    }
}

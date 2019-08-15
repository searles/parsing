package at.searles.parsing;

import at.searles.lexer.Lexer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.EmptyConcreteSyntaxTree;
import at.searles.parsing.utils.common.ToString;
import at.searles.regex.RegexParser;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CombinatorTest {

    private final Lexer lexer = new Lexer();
    private final Parser<String> chr = Parser.fromToken(lexer.token(RegexParser.parse("[a-z]")), new ToString(), false);
    private final Recognizer comma = Recognizer.fromString(",", lexer, false);
    private final Initializer<String> emptyString = new Initializer<String>() {
        @Override
        public String parse(ParserCallBack env, ParserStream stream) {
            return "";
        }

        @Override
        public ConcreteSyntaxTree print(PrinterCallBack env, String s) {
            return s.isEmpty() ? new EmptyConcreteSyntaxTree() : null;
        }
    };
    private final Fold<String, String, String> appendSingleChar = new Fold<String, String, String>() {
        @Override
        public String apply(ParserCallBack env, ParserStream stream, @NotNull String left, @NotNull String right) {
            return left + right;
        }

        @Override
        public String leftInverse(PrinterCallBack env, @NotNull String result) {
            return !result.isEmpty() ? result.substring(0, result.length() - 1) : null;
        }

        @Override
        public String rightInverse(PrinterCallBack env, @NotNull String result) {
            return !result.isEmpty() ? result.substring(result.length() - 1) : null;
        }

        @Override
        public String toString() {
            return "{append_single_char}";
        }
    };
    private Parser<String> parser;
    private ParserStream input;
    private String parseResult;
    private ConcreteSyntaxTree printResult;
    private boolean error;
    private final ParserCallBack env = new ParserCallBack() {
        @Override
        public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
            error = true;
            throw new IllegalArgumentException();
        }
    };
    private final PrinterCallBack env2 = new PrinterCallBack() {
        @Override
        public void notifyLeftPrintFailed(ConcreteSyntaxTree rightTree, Recognizable.Then failed) {
            error = true;
            throw new IllegalArgumentException();
        }
    };

    @Before
    public void setUp() {
        error = false;
    }

    // These tests run on a parser that simply parses single chars and
    // attaches these single chars using a simple fold.
    @Test
    public void plus1FailTest() {
        // chr+
        withParser(emptyString.then(Reducer.plus(chr.fold(appendSingleChar))));
        withInput("");
        actParse();

        Assert.assertTrue(error);
        Assert.assertNull(parseResult);
    }

    @Test
    public void plus1SuccessTest() {
        // chr+
        withParser(emptyString.then(Reducer.plus(chr.fold(appendSingleChar))));
        withInput("abc");
        actParse();
        actPrint();

        Assert.assertFalse(error);
        Assert.assertEquals("abc", parseResult);
        Assert.assertEquals("abc", printResult.toString());
    }

    @Test
    public void joinFailTest() {
        // chr+
        withParser(emptyString.then(comma.join(chr.fold(appendSingleChar))));
        withInput("a,,");
        actParse();

        Assert.assertTrue(error);
    }

    @Test
    public void joinSingleCharTest() {
        // chr+
        withParser(emptyString.then(comma.join(chr.fold(appendSingleChar))));
        withInput("a");
        actParse();
        actPrint();

        Assert.assertFalse(error);
        Assert.assertEquals("a", parseResult);
        Assert.assertEquals("a", printResult.toString());
    }

    @Test
    public void joinMultiCharTest() {
        // chr+
        withParser(emptyString.then(comma.join(chr.fold(appendSingleChar))));
        withInput("a,b,c");
        actParse();
        actPrint();

        Assert.assertFalse(error);
        Assert.assertEquals("abc", parseResult);
        Assert.assertEquals("a,b,c", printResult.toString());
    }

    private void actPrint() {
        try {
            printResult = parser.print(env2, parseResult);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void actParse() {
        try {
            parseResult = parser.parse(env, input);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void withInput(String input) {
        this.input = ParserStream.fromString(input);
    }

    private void withParser(Parser<String> parser) {
        this.parser = parser;
    }
}

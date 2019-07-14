package at.searles.parsing;

import at.searles.lexer.Lexer;
import at.searles.parsing.printing.EmptyStringTree;
import at.searles.parsing.printing.StringTree;
import at.searles.parsing.utils.common.Str;
import at.searles.regex.RegexParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CombinatorTest {

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

    private Lexer lexer = new Lexer();
    private Parser<String> chr =  Parser.fromToken(lexer.token(RegexParser.parse("[a-z]")), new Str(), false);
    private Recognizer comma = Recognizer.fromString(",", lexer, false);

    private Initializer<String> emptyString = new Initializer<String>() {
        @Override
        public String parse(Environment env, ParserStream stream) {
            return "";
        }

        @Override
        public StringTree print(Environment env, String s) {
            return s.isEmpty() ? new EmptyStringTree() : null;
        }
    };

    private Fold<String, String, String> appendSingleChar = new Fold<String, String, String>() {
        @Override
        public String apply(Environment env, String left, String right, ParserStream stream) {
            return left + right;
        }

        @Override
        public String leftInverse(Environment env, String result) {
            return !result.isEmpty() ? result.substring(0, result.length() - 1) : null;
        }

        @Override
        public String rightInverse(Environment env, String result) {
            return !result.isEmpty() ? result.substring(result.length() - 1) : null;
        }

        @Override
        public String toString() {
            return "{append_single_char}";
        }
    };

    private Environment env = new Environment() {
        @Override
        public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
            error = true;
            throw new IllegalArgumentException();
        }

        @Override
        public void notifyLeftPrintFailed(StringTree rightTree, Recognizable.Then failed) {
            error = true;
            throw new IllegalArgumentException();
        }
    };

    private Parser<String> parser;

    private ParserStream input;
    private String parseResult;
    private StringTree printResult;
    private boolean error;

    private void actPrint() {
        try {
            printResult = parser.print(env, parseResult);
        } catch(IllegalArgumentException e) {
        }
    }

    private void actParse() {
        try {
            parseResult = parser.parse(env, input);
        } catch(IllegalArgumentException e) {
        }
    }

    private void withInput(String input) {
        this.input = ParserStream.fromString(input);
    }

    private void withParser(Parser<String> parser) {
        this.parser = parser;
    }
}

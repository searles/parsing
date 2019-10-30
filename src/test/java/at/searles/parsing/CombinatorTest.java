package at.searles.parsing;

import at.searles.lexer.Lexer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.EmptyConcreteSyntaxTree;
import at.searles.parsing.printing.PrinterBacktrackException;
import at.searles.regex.RegexParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CombinatorTest {
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

    private final Lexer tokenizer = new Lexer();
    private final Parser<String> chr = Parser.fromRegex(RegexParser.parse("[a-z]"), tokenizer, false, ToString);
    private final Recognizer comma = Recognizer.fromString(",", tokenizer, false);
    private final Initializer<String> emptyString = new Initializer<String>() {
        @Override
        public String parse(ParserStream stream) {
            return "";
        }

        @Override
        public ConcreteSyntaxTree print(String s) {
            return s.isEmpty() ? new EmptyConcreteSyntaxTree() : null;
        }
    };
    private final Fold<String, String, String> appendSingleChar = new Fold<String, String, String>() {
        @Override
        public String apply(ParserStream stream, @NotNull String left, @NotNull String right) {
            return left + right;
        }

        @Override
        public String leftInverse(@NotNull String result) {
            return !result.isEmpty() ? result.substring(0, result.length() - 1) : null;
        }

        @Override
        public String rightInverse(@NotNull String result) {
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

        Assert.assertFalse(error);
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
            printResult = parser.print(parseResult);
        } catch (PrinterBacktrackException ignored) {
        }
    }

    private void actParse() {
        try {
            parseResult = parser.parse(input);
        } catch (ParserLookaheadException ignored) {
            error = true;
        }
    }

    private void withInput(String input) {
        this.input = ParserStream.fromString(input);
    }

    private void withParser(Parser<String> parser) {
        this.parser = parser;
    }
}

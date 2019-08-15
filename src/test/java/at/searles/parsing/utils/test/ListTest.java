package at.searles.parsing.utils.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.utils.ImmutableList;
import at.searles.parsing.utils.Utils;
import at.searles.regex.RegexParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Test grouping of items with same type: B1,2,3:Aa,b:B1:B1,2:Aa:Ab
 * use case: Grouping of var declarations in program declarations.
 */
public class ListTest {

    private final Lexer lexer = new Lexer();
    private final Parser<Object> id = Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")), new Mapping<CharSequence, Object>() {
        @Override
        public Object parse(ParserCallBack env, ParserStream stream, @NotNull CharSequence left) {
            return left.toString();
        }

        @Nullable
        @Override
        public CharSequence left(PrinterCallBack env, @NotNull Object result) {
            return result instanceof String ? result.toString() : null;
        }
    }, false);
    private final Parser<Object> num = Parser.fromToken(lexer.token(RegexParser.parse("[0-9]+")), new Mapping<CharSequence, Object>() {
        @Override
        public Object parse(ParserCallBack env, ParserStream stream, @NotNull CharSequence left) {
            return Integer.parseInt(left.toString());
        }

        @Nullable
        @Override
        public CharSequence left(PrinterCallBack env, @NotNull Object result) {
            return result instanceof Integer ? result.toString() : null;
        }
    }, false);
    private final Fold<List<Object>, Object, List<Object>> add = new Fold<List<Object>, Object, List<Object>>() {
        @Override
        public List<Object> apply(ParserCallBack env, ParserStream stream, @NotNull List<Object> left, @NotNull Object right) {
            return ImmutableList.createFrom(left).pushBack(right);
        }

        @Override
        public List<Object> leftInverse(PrinterCallBack env, @NotNull List<Object> result) {
            if (rightInverse(env, result) == null) {
                return null;
            }

            return result.subList(0, result.size() - 1);
        }

        @Override
        public Object rightInverse(PrinterCallBack env, @NotNull List<Object> result) {
            if (result.isEmpty()) {
                return null;
            }

            return result.get(result.size() - 1);
        }
    };
    private final Recognizer comma = Recognizer.fromString(",", lexer, false);
    private final Recognizer colon = Recognizer.fromString(":", lexer, false);
    private final Recognizer stringsPrefix = Recognizer.fromString("S", lexer, false);
    private final Recognizer intsPrefix = Recognizer.fromString("I", lexer, false);
    private final Reducer<List<Object>, List<Object>> strings = stringsPrefix.then(comma.joinPlus(id.fold(add)));
    private final Reducer<List<Object>, List<Object>> ints = intsPrefix.then(comma.joinPlus(num.fold(add)));
    private final Parser<List<Object>> parser =
            Utils.empty().then(
                    colon.join(
                            strings.or(ints)
                    )
            );
    private final ParserCallBack env = new ParserCallBack() {
        @Override
        public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
            throw new IllegalArgumentException();
        }
    };
    private final PrinterCallBack env2 = (a, b) -> {throw new IllegalArgumentException();};
    private ParserStream input;
    private List<Object> item;
    private String output;

    @Test
    public void testOneElementEach() {
        withInput("Sa:I1");
        actParse();
        actPrint();

        Assert.assertEquals("Sa:I1", output);
    }

    @Test
    public void testMultiple() {
        withInput("Sa:Sb:Sc");
        actParse();
        actPrint();

        Assert.assertEquals("Sa,b,c", output);
    }

    @Test
    public void testMultipleEach() {
        withInput("Sa:Sb,c:I1,2,3:I4:Sd,e:Sf:I5:I6");
        actParse();
        actPrint();

        Assert.assertEquals("Sa,b,c:I1,2,3,4:Sd,e,f:I5,6", output);
    }

    private void actPrint() {
        ConcreteSyntaxTree tree = parser.print(env2, item);
        output = tree != null ? tree.toString() : null;
    }

    private void actParse() {
        item = parser.parse(env, input);
    }

    private void withInput(String input) {
        this.input = ParserStream.fromString(input);

    }
}

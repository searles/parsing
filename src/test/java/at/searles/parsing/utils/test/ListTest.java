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



    private Lexer lexer = new Lexer();

    private Parser<Object> id = Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")), new Mapping<CharSequence, Object>() {
        @Override
        public Object parse(Environment env, @NotNull CharSequence left, ParserStream stream) {
            return left.toString();
        }

        @Nullable
        @Override
        public CharSequence left(Environment env, @NotNull Object result) {
            return result instanceof String ? result.toString() : null;
        }
    }, false);

    private Parser<Object> num = Parser.fromToken(lexer.token(RegexParser.parse("[0-9]+")), new Mapping<CharSequence, Object>() {
        @Override
        public Object parse(Environment env, @NotNull CharSequence left, ParserStream stream) {
            return Integer.parseInt(left.toString());
        }

        @Nullable
        @Override
        public CharSequence left(Environment env, @NotNull Object result) {
            return result instanceof Integer ? result.toString() : null;
        }
    }, false);

    private Fold<List<Object>, Object, List<Object>> add = new Fold<List<Object>, Object, List<Object>>() {
        @Override
        public List<Object> apply(Environment env, List<Object> left, Object right, ParserStream stream) {
            return ImmutableList.createFrom(left).pushBack(right);
        }

        @Override
        public List<Object> leftInverse(Environment env, List<Object> result) {
            if(rightInverse(env, result) == null) {
                return null;
            }

            return result.subList(0, result.size() - 1);
        }

        @Override
        public Object rightInverse(Environment env, List<Object> result) {
            if(result.isEmpty()) {
                return null;
            }

            return result.get(result.size() - 1);
        }
    };

    private Recognizer comma = Recognizer.fromString(",", lexer, false);
    private Recognizer colon = Recognizer.fromString(":", lexer, false);

    private Recognizer stringsPrefix = Recognizer.fromString("S", lexer, false);
    private Recognizer intsPrefix = Recognizer.fromString("I", lexer, false);

    private Reducer<List<Object>, List<Object>> strings = stringsPrefix.then(comma.joinPlus(id.fold(add)));
    private Reducer<List<Object>, List<Object>> ints = intsPrefix.then(comma.joinPlus(num.fold(add)));

    private Parser<List<Object>> parser =
            Utils.empty().then(
                colon.join(
                        strings.or(ints)
                )
            );

    private Environment env = new Environment() {
        @Override
        public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
            throw new IllegalArgumentException();
        }

        @Override
        public void notifyLeftPrintFailed(ConcreteSyntaxTree rightTree, Recognizable.Then failed) {
            throw new IllegalArgumentException();
        }
    };

    private ParserStream input;
    private List<Object> item;
    private String output;

    private void actPrint() {
        ConcreteSyntaxTree tree = parser.print(env, item);
        output = tree != null ? tree.toString(): null;
    }

    private void actParse() {
        item = parser.parse(env, input);
    }

    private void withInput(String input) {
        this.input = ParserStream.fromString(input);

    }
}

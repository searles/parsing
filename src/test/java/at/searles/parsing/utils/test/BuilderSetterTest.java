package at.searles.parsing.utils.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.utils.Utils;
import at.searles.regex.RegexParser;
import at.searles.utils.GenericBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

public class BuilderSetterTest {

    private Lexer lexer = new Lexer();
    private Parser<Object> id =  Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")),
            new Mapping<CharSequence, Object>() {
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

    private Environment env = new Environment() {
        @Override
        public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
            throw new IllegalArgumentException();
        }

        @Override
        public void notifyLeftPrintFailed(ConcreteSyntaxTree rightTree, Recognizable.Then failed) {
        }
    };

    Parser<Object> parser =
            id.then(
                Reducer.opt(
                    Recognizer.fromString("+", lexer, false)
                    .then(Utils.builder(Builder.class, "a"))
                    .then(Utils.build(Builder.class))
                ));

    private ParserStream input;
    private Object item; // using object to test inheritance
    private String output;

    @Test
    public void testNoOpt() {
        withInput("k");
        actParse();

        Assert.assertTrue(item instanceof String);
    }

    @Test
    public void testOpt() {
        withInput("k+");
        actParse();

        Assert.assertTrue(item instanceof Item);
    }

    @Test
    public void testOptPrint() {
        withInput("k+");
        actParse();
        actPrint();

        Assert.assertEquals("k+", output);
    }

    @Test
    public void testNoOptPrint() {
        withInput("k");
        actParse();
        actPrint();

        Assert.assertEquals("k", output);
    }

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

    public static class Item {
        public final String a;

        public Item(String a) {
            this.a = a;
        }
    }

    public static class Builder extends GenericBuilder<Builder, Item> {
        public String a;

        public static Builder toBuilder(Item item) {
            Builder builder = new Builder();

            builder.a = item.a;

            return builder;
        }

        @Override
        public Item build(Environment env, ParserStream stream) {
            return new Item(a);
        }
    }
}

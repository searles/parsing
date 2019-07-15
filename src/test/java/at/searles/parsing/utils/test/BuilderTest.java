package at.searles.parsing.utils.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.parsing.printing.StringTree;
import at.searles.parsing.utils.Utils;
import at.searles.parsing.utils.common.Str;
import at.searles.regex.RegexParser;
import at.searles.utils.GenericBuilder;
import org.junit.Assert;
import org.junit.Test;

public class BuilderTest {

    private Lexer lexer = new Lexer();
    private Parser<String> id =  Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")), new Str(), false);

    private Environment env = new Environment() {
        @Override
        public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
            throw new IllegalArgumentException();
        }

        @Override
        public void notifyLeftPrintFailed(StringTree rightTree, Recognizable.Then failed) {
        }
    };

    Parser<Item> parser = Utils.builder(Builder.class).then(
            Recognizer.fromString(",", lexer, false).join(
                Utils.<Builder, String>setter("a", Recognizer.fromString("+", lexer, false).then(id))
                .or(Utils.setter("b", Recognizer.fromString("-", lexer, false).then(id)), true)
            )
        .then(Utils.apply(Builder.class, Item.class))
    );

    private ParserStream input;
    private Item item;
    private String output;

    @Test
    public void testEmpty() {
        withString("");
        actParse();
        actPrint();

        Assert.assertEquals("", output);
    }

    @Test
    public void testAB() {
        withString("+zyx,-wvu");
        actParse();
        actPrint();

        Assert.assertEquals("+zyx,-wvu", output);
    }

    @Test
    public void testAA() {
        withString("+zyx,+wvu");
        actParse();
        actPrint();

        Assert.assertEquals("+wvu", output);
    }

    private void actPrint() {
        output = parser.print(env, item).toString();
    }

    private void actParse() {
        item = parser.parse(env, input);
    }

    private void withString(String input) {
        this.input = ParserStream.fromString(input);

    }

    public static class Item {
        public final String a;
        public final String b;

        public Item(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class Builder extends GenericBuilder<Builder> {
        public String a;
        public String b;

        public static Builder toBuilder(Item item) {
            // may return null if not applicable
            Builder builder = new Builder();
            builder.a = item.a;
            builder.b = item.b;

            return builder;
        }

        public Item build(ParserStream stream) {
            return new Item(a, b);
        }
    }
}

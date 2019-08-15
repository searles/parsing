package at.searles.parsing.utils.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.utils.Utils;
import at.searles.parsing.utils.common.ToString;
import at.searles.regex.RegexParser;
import at.searles.utils.GenericStruct;
import org.junit.Assert;
import org.junit.Test;

public class BuilderTest {

    private final Lexer lexer = new Lexer();
    private final Parser<String> id = Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")), new ToString(), false);
    final Parser<Object> parser = Utils.builder(Builder.class).then(
            Recognizer.fromString(",", lexer, false).join(
                    Utils.<Builder, String>setter("a", Recognizer.fromString("+", lexer, false).then(id))
                            .or(Utils.setter("b", Recognizer.fromString("-", lexer, false).then(id)), true)
            )
                    .then(Utils.build(Builder.class))
    );

    private ParserStream input;
    private Object item; // using object to test inheritance
    private String output;

    @Test
    public void testEmpty() {
        withInput("");
        actParse();
        actPrint();

        Assert.assertEquals("", output);
    }

    @Test
    public void testAB() {
        withInput("+zyx,-wvu");
        actParse();
        actPrint();

        Assert.assertEquals("+zyx,-wvu", output);
    }

    @Test
    public void testAA() {
        withInput("+zyx,+wvu");
        actParse();
        actPrint();

        Assert.assertEquals("+wvu", output);
    }

    @Test
    public void testBadObject() {
        this.item = "This is a string and not an item";
        actPrint();

        Assert.assertNull(output);
    }

    private void actPrint() {
        ConcreteSyntaxTree tree = parser.print(item);
        output = tree != null ? tree.toString() : null;
    }

    private void actParse() {
        item = parser.parse(input);
    }

    private void withInput(String input) {
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

    public static class Builder extends GenericStruct<Builder> {
        public String a;
        public String b;

        public static Builder toBuilder(Item item) {
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

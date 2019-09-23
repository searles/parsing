package at.searles.parsing.utils.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.utils.Utils;
import at.searles.regex.RegexParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

public class CreatorPutTest {
    private final Lexer tokenizer = new Lexer();
    private final Parser<Object> id = Parser.fromRegex(RegexParser.parse("[a-z]+"),
            tokenizer, false,
            new Mapping<CharSequence, Object>() {
                @Override
                public Object parse(ParserStream stream, @NotNull CharSequence left) {
                    return left.toString();
                }

                @Nullable
                @Override
                public CharSequence left(@NotNull Object result) {
                    return result instanceof String ? result.toString() : null;
                }
            });
    final Parser<Item> parser =
            id
            .then(Utils.properties("a"))
            .then(Utils.create(Item.class, "a")
            );

    private ParserStream input;
    private Item item; // using object to test inheritance
    private String output;

    @Test
    public void testPrint() {
        withInput("k");
        actParse();
        actPrint();

        Assert.assertEquals("k", output);
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
        private final String a;

        public Item(String a) {
            this.a = a;
        }
    }
}

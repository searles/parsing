package at.searles.parsing.utils.test;

import at.searles.lexer.Lexer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.utils.Utils;
import at.searles.parsing.utils.ast.SourceInfo;
import at.searles.parsing.utils.builder.Properties;
import at.searles.parsing.utils.common.ToString;
import at.searles.regex.RegexParser;
import org.junit.Assert;
import org.junit.Test;

public class CreatorTest {

    private final Lexer tokenizer = new Lexer();

    private final Parser<String> id = Parser.fromRegex(RegexParser.parse("[a-z]+"), tokenizer, false, new ToString());

    final Parser<Properties> propertiesParser = Utils.properties().then(
            Recognizer.fromString(",", tokenizer, false).join(
                    Utils.put("a", Recognizer.fromString("+", tokenizer, false).then(id))
                    .or(Utils.put("b", Recognizer.fromString("-", tokenizer, false).then(id)), true)
            )
    );

    final Parser<Item1> parser1 = propertiesParser.then(Utils.create(Item1.class, "a", "b"));
    final Parser<Item2> parser2 = propertiesParser.then(Utils.create(Item2.class, "a", "b"));
    final Parser<Item3> parser3 = propertiesParser.then(Utils.create(Item3.class, "a", "b"));

    private ParserStream input;
    private String output;

    @Test
    public void testEmpty1() {
        withInput("");

        Item1 item = actParse(parser1);
        actPrint(parser1, item);

        Assert.assertEquals("", output);
    }

    @Test
    public void testAB1() {
        withInput("+zyx,-wvu");
        Item1 item = actParse(parser1);
        actPrint(parser1, item);

        Assert.assertEquals("+zyx,-wvu", output);
    }

    @Test
    public void testAA1() {
        withInput("+zyx,+wvu");
        Item1 item = actParse(parser1);
        actPrint(parser1, item);

        Assert.assertEquals("+wvu", output);
    }

    @Test
    public void testEmpty2() {
        withInput("");

        Item2 item = actParse(parser2);
        actPrint(parser2, item);

        Assert.assertEquals("", output);
    }

    @Test
    public void testAB2() {
        withInput("+zyx,-wvu");
        Item2 item = actParse(parser2);
        actPrint(parser2, item);

        Assert.assertEquals("+zyx,-wvu", output);
    }

    @Test
    public void testAA2() {
        withInput("+zyx,+wvu");
        Item2 item = actParse(parser2);
        actPrint(parser2, item);

        Assert.assertEquals("+wvu", output);
    }

    @Test
    public void testEmpty3() {
        withInput("");

        Item3 item = actParse(parser3);
        actPrint(parser3, item);

        Assert.assertEquals("", output);
    }

    @Test
    public void testAB3() {
        withInput("+zyx,-wvu");
        Item3 item = actParse(parser3);
        actPrint(parser3, item);

        Assert.assertEquals("+zyx,-wvu", output);
    }

    @Test
    public void testAA3() {
        withInput("+zyx,+wvu");
        Item3 item = actParse(parser3);
        actPrint(parser3, item);

        Assert.assertEquals("+wvu", output);
    }

    private <T> void actPrint(Parser<T> parser, T item) {
        ConcreteSyntaxTree tree = parser.print(item);
        output = tree != null ? tree.toString() : null;
    }

    private <T> T actParse(Parser<T> parser) {
        return parser.parse(input);
    }

    private void withInput(String input) {
        this.input = ParserStream.fromString(input);

    }

    public static class Item1 {
        private final SourceInfo info;

        private final String mA;
        private final String mB;

        public Item1(SourceInfo info, String a, String b) {
            this.info = info;
            this.mA = a;
            this.mB = b;
        }

        public String getA() {
            return mA;
        }

        public String getB() {
            return mB;
        }
    }

    public static class Item2 {
        private final String a;
        private final String b;

        public Item2(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class Item3 {
        private String mA;
        private String mB;

        public String getA() {
            return mA;
        }

        public void setA(String a) {
            this.mA = a;
        }

        public String getB() {
            return mB;
        }

        public void setB(String b) {
            this.mB = b;
        }
    }
}

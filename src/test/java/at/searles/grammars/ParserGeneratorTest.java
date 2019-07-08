package at.searles.grammars;

import at.searles.buf.BufferedStream;
import at.searles.buf.FrameStreamImpl;
import at.searles.buf.ReaderCharStream;
import at.searles.lexer.TokStream;
import at.searles.parsing.Environment;
import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizable;
import at.searles.parsing.utils.ast.AstNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ParserGeneratorTest {

    private ParserStream stream;
    private ParserGenerator generator;
    private Environment env;

    private boolean error = false;
    private AstNode expr;
    private List<AstNode> ruleSet;
    private AstNode rule;

    @Before
    public void setup() {
        this.generator = new ParserGenerator();
        this.env = new Environment() {
            @Override
            public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
                error = true;
                System.err.print("at '" + stream + "' when parsing '" + failedParser);
            }
        };
        this.error = false;
    }

    @Test
    public void testInteger() {
        withString("DIGIT DIGIT*");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testString() {
        withString("'abc'");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testOr() {
        withString("A | B");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testCharSet() {
        withString("[a-zA-Z0-9_]*");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testSimpleRule() {
        withString("rule:head");
        parseRule();
        Assert.assertNotNull(rule);
    }

    @Test
    public void testNewLine() {
        withString("rule: a \n | b \n | c ;");
        parseRuleSet();
        Assert.assertEquals(1, ruleSet.size());
    }

    @Test
    public void testHidden() {
        withString("/*start*/a /* concat */ b // Hello \n\r\t | c");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testJavaRef() {
        withString("`Hel``lo`");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testJavaRefConcat() {
        withString("`Hel``lo`Abc");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void testParseJava8AntlrGrammar() throws FileNotFoundException {
        withFile("src/test/java/at/searles/grammars/Java8.grammar");
        parseRuleSet();
        Assert.assertEquals(391, ruleSet.size());
    }

    @Test
    public void invSet(){
        withString("~[a-c]");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void unicodeSmallUString(){
        withString("'\\u0000'");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void unicodeLargeUString(){
        withString("'\\U00000000'");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void unicodeXString(){
        withString("'\\U00000000'");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    @Test
    public void unicodeSet(){
        withString("[\\u0000-\\u007F\\uD800-\\uDBFF]");
        parseExpr();
        Assert.assertNotNull(expr);
    }

    private void withFile(String filename) throws FileNotFoundException {
        FileReader fileReader = new FileReader(filename);

        this.stream = new ParserStream(new TokStream(new FrameStreamImpl(new BufferedStream.Impl(new ReaderCharStream(fileReader), 1024 * 1024))));
    }

    private void withString(String input) {
        stream = ParserStream.fromString(input);
    }

    private void parseExpr() {
        this.expr = generator.expr(env, stream);
    }

    private void parseRuleSet() {
        this.ruleSet = generator.rules(env, stream);
    }

    private void parseRule() {
        this.rule = generator.rule(env, stream);
    }
}

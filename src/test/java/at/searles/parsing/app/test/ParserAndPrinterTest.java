package at.searles.parsing.app.test;

import at.searles.buf.CharStream;
import at.searles.lexer.Lexer;
import at.searles.lexer.TokStream;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.regex.CharSet;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class ParserAndPrinterTest {

    private Parser<Expr> parser;
    private Environment env;
    private ParserStream input;
    private Expr result;
    private ConcreteSyntaxTree output;

    @Before
    public void setUp() {
        this.env = new Environment() {
            @Override
            public void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser) {
                throw new IllegalArgumentException();
            }

            @Override
            public void notifyLeftPrintFailed(ConcreteSyntaxTree rightTree, Recognizable.Then failed) {
                throw new IllegalArgumentException();
            }
        };
    }


    @Test
    public void testIdIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE);
        withInput("a");

        // act
        parse();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("a", result.toString());
    }

    @Test
    public void testIdRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE);
        withInput("a");

        // act
        parse();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("a", result.toString());
    }

    @Test
    public void testIdWrappedIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE);
        withInput("((a))");

        // act
        parse();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("a", result.toString());
    }

    @Test
    public void testIdWrappedRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE);
        withInput("((a))");

        // act
        parse();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("a", result.toString());
    }

    @Test
    public void testSimpleAppIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE);
        withInput("ab");

        // act
        parse();
        print();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("ab", output.toString());
    }

    @Test
    public void testSimpleAppRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE);
        withInput("ab");

        // act
        parse();
        print();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("ab", output.toString());
    }

    @Test
    public void testLongAppIterativeParser() {
        // prepare
        withParser(Parsers.ITERATIVE);
        withInput("abc(def)");

        // act
        parse();
        print();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("abc(def)", output.toString());
    }

    @Test
    public void testLongAppRecursiveParser() {
        // prepare
        withParser(Parsers.RECURSIVE);
        withInput("abc(def)");

        // act
        parse();
        print();

        // test
        Assert.assertNotNull(result);
        // fixme Assert.assertTrue(input.isEmpty());
        Assert.assertEquals("abcdef", output.toString());
    }

    @Test
    public void testIterativeParserRecursivePrinter() {
        // prepare
        withParser(Parsers.ITERATIVE);
        withInput("abcde");

        // act
        parse();

        withParser(Parsers.RECURSIVE);
        print();

        // test
        Assert.assertNotNull(result);
        Assert.assertEquals("(((ab)c)d)e", output.toString());
    }

    @Test
    public void testRecursiveParserIterativePrinter() {
        // prepare
        withParser(Parsers.RECURSIVE);
        withInput("abcde");

        // act
        parse();

        withParser(Parsers.ITERATIVE);
        print();

        // test
        Assert.assertNotNull(result);
        // fixme Assert.assertTrue(input.isEmpty());
        Assert.assertEquals("a(b(c(de)))", output.toString());
    }

    @Test
    public void testLotsOfData() {
        // about 3.5 seconds for 1000000
        // about 35 seconds for 10000000
        this.input = new ParserStream(TokStream.fromCharStream(stream(1000000)));
        withParser(Parsers.ITERATIVE);
        parse();

        System.out.println("Parser successful");

        print();

        System.out.println("Printer successful");

        String str = output.toString();

        System.out.println(str.length());

        withInput(str);
        parse();

        System.out.println("Parsing output successful");

        print();

        String str2 = output.toString();

        Assert.assertEquals(str, str2);
        //System.out.println(this.output);
    }


    private CharStream stream(int sizeLimit) {
        return new CharStream() {
            int countOpen = 0;
            int count = 0;
            final Random rnd = new Random();

            boolean justOpened = true;

            @Override
            public int next() {
                int random = Math.abs(rnd.nextInt());

                if (count > sizeLimit && !justOpened) {
                    if (countOpen > 0) {
                        countOpen--;
                        return ')';
                    }

                    return -1;
                }

                count++;

                if(count > sizeLimit)
                    random = random % 26;
                else
                    random = random % 40;

                if(random < 26) {
                    justOpened = false;
                    return random + 'a';
                }

                if(countOpen > 0 && random % 3 != 0 && !justOpened) {
                    countOpen--;
                    return ')';
                }

                justOpened = true;
                countOpen++;
                return '(';
            }
        };
    }


    private void withInput(String input) {
        this.input = ParserStream.fromString(input);
    }

    private void parse() {
        this.result = parser.parse(env, input);
    }

    private void print() {
        assert result != null;

        this.output = parser.print(env, result);
    }

    private void withParser(Parser<Expr> parser) {
        this.parser = parser;
    }

    private enum Parsers implements Parser<Expr> {
        RECURSIVE {
            @Override
            public boolean recognize(Environment env, ParserStream stream) {
                return false;
            }

            final Ref<Expr> exprParser = new Ref<>("expr");

            final Parser<Expr> term = term(exprParser);

            final Reducer<Expr, Expr> exprReducer = exprParser.fold(
                    new Fold<Expr, Expr, Expr>() {

                        @Override
                        public Expr apply(Environment env, ParserStream stream, @NotNull Expr left, @NotNull Expr right) {
                            return left.app(right);
                        }

                        @Override
                        public Expr leftInverse(Environment env, @NotNull Expr result) {
                            return result.left();
                        }

                        @Override
                        public Expr rightInverse(Environment env, @NotNull Expr result) {
                            return result.right();
                        }
                    }
            );

            // this one is recursive, hence
            { exprParser.set(term.then(Reducer.opt(exprReducer))); }

            @Override
            public Expr parse(Environment environment, ParserStream stream) {
                return exprParser.parse(environment, stream);
            }

            @Override
            public ConcreteSyntaxTree print(Environment environment, Expr expr) {
                return exprParser.print(environment, expr);
            }
        }
        ,
        ITERATIVE {
            @Override
            public boolean recognize(Environment env, ParserStream stream) {
                return false;
            }

            final Ref<Expr> exprParser = new Ref<>("expr");

            final Parser<Expr> term = term(exprParser);

            final Reducer<Expr, Expr> appReducer = term.fold(
                    new Fold<Expr, Expr, Expr>() {
                        @Override
                        public Expr apply(Environment env, ParserStream stream, @NotNull Expr left, @NotNull Expr right) {
                            return left.app(right);
                        }

                        @Override
                        public Expr leftInverse(Environment env, @NotNull Expr result) {
                            return result.left();
                        }

                        @Override
                        public Expr rightInverse(Environment env, @NotNull Expr result) {
                            return result.right();
                        }
                    });

            {
                exprParser.set(term.then(Reducer.rep(appReducer)));
            }


            @Override
            public Expr parse(Environment environment, ParserStream stream) {
                return exprParser.parse(environment, stream);
            }

            @Override
            public ConcreteSyntaxTree print(Environment environment, Expr expr) {
                return exprParser.print(environment, expr);
            }
        },
        ;

        Parser<Expr> term(Ref<Expr> exprParser) {
            Lexer lexer = new Lexer();
            Parser<Expr> idParser =
                    Parser.fromToken(lexer.token(CharSet.interval('a', 'z')),
                    new Mapping<CharSequence, Expr>() {
                        @NotNull
                        @Override
                        public Id parse(Environment env, ParserStream stream, @NotNull CharSequence left) {
                            return new Id(left.toString());
                        }

                        @Override
                        public CharSequence left(Environment env, @NotNull Expr result) {
                            return result.id();
                        }
                    }, false);

            Parser<Expr> wrappedExprParser =
                    Recognizer.fromString("(", lexer, false).
                            then(exprParser).then(Recognizer.fromString(")", lexer, false));

            return idParser.or(wrappedExprParser);
        }
    }

}

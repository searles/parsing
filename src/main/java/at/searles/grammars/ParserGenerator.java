package at.searles.grammars;

import at.searles.lexer.Lexer;
import at.searles.lexer.LexerWithHidden;
import at.searles.parsing.*;
import at.searles.parsing.utils.Utils;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.builder.AstNodeBuilder;
import at.searles.regex.CharSet;
import at.searles.regex.Regex;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ParserGenerator {

    private final Ref<AstNode> rule = new Ref<>(Type.Rule.toString());
    private final Ref<AstNode> expr = new Ref<>(Type.Expr.toString());
    private final Ref<AstNode> concat = new Ref<>(Type.Concat.toString());
    private final Ref<AstNode> folded = new Ref<>(Type.Fold.toString());
    private final Ref<AstNode> annotated = new Ref<>(Type.Annotate.toString());
    private final Ref<AstNode> literal = new Ref<>(Type.Literal.toString());
    private final Ref<AstNode> term = new Ref<>(Type.Term.toString());
    private final Ref<AstNode> text = new Ref<>(Type.Text.toString());
    private final Ref<AstNode> charSet = new Ref<>(Type.CharSet.toString());
    private final Ref<Integer> escChar = new Ref<>(Type.EscChar.toString());
    private final Ref<AstNode> ref = new Ref<>(Type.Reference.toString());
    private final Ref<String> identifier = new Ref<>(Type.Identifier.toString());
    private final Ref<String> javaCode = new Ref<>(Type.JavaCode.toString());
    private final LexerWithHidden lexer = new LexerWithHidden();
    private final Lexer rawLexer = new Lexer();
    private final AstNodeBuilder<Type> builder = new SyntaxTreeBuilder();

    public ParserGenerator() {
        initHidden();
        initCharSet();
        initEscChars();
        initText();
        initRef();
        init();
    }

    public List<AstNode> rules(ParserStream input) {
        return Utils.list(rule.then(Recognizer.fromString(";", lexer, false))).parse(input);
    }

    public AstNode rule(ParserStream stream) {
        return rule.parse(stream);
    }

    public AstNode expr(ParserStream input) {
        return expr.parse(input);
    }

    private Recognizer t(String str) {
        return Recognizer.fromString(str, lexer, false);
    }

    private void initHidden() {
        // hidden tokens
        lexer.hiddenToken(CharSet.chars(' ', '\n', '\r', '\t').plus());
        lexer.hiddenToken(Regex.text("//").then(CharSet.all().rep()).then(CharSet.chars('\n', '\r')).nonGreedy());
        lexer.hiddenToken(Regex.text("/*").then(CharSet.all().rep()).then(Regex.text("*/")).nonGreedy());
    }

    private void init() {
        // rule: IDENTIFIER ':'expr ;

        rule.set(
                identifier.then(t(":"))
                        .then(
                                expr
                                        .or(builder.empty(Type.None))
                                        .fold(builder.binary(Type.Rule))
                        )
        );

        // expr: concat ('|'concat)* ;

        expr.set(concat
                .then(Reducer.rep(
                        t("|").then(concat)
                                .fold(builder.binary(Type.Choice))
                ))
        );

        // concat: lit lit* ;

        concat.set(annotated
                .then(Reducer.rep(
                        annotated
                                .fold(builder.binary(Type.Concat))
                ))
        );

        annotated.set(
                folded.then(
                        Reducer.opt(t("@").then(
                                javaCode
                                        .fold(builder.binary(Type.Annotate))
                        ))
                )
        );

        folded.set(
                literal.then(
                        Reducer.opt(t(">").then(
                                javaCode
                                        .fold(builder.binary(Type.Fold))
                        ))
                )
        );

        // lit: term ('?'|'+'|'*'|'^'|'{' num (',' num?)? '}')?;

        Parser<Integer> num = Parser.fromToken(
                lexer.token(CharSet.interval('0', '9').range(1, 6)),
                (stream, str) -> Integer.parseInt(str.toString()), false);

        Mapping<Integer, int[]> countMapping = (stream, count) -> new int[]{count};
        Mapping<Integer, int[]> minMapping = (stream, count) -> new int[]{count, -1};
        Fold<Integer, Integer, int[]> minMaxFold = (stream, left, right) -> new int[]{left, right};

        Parser<int[]> range = num.then(
                t(",").then(
                        num.fold(minMaxFold)
                                .or(minMapping)
                )
                        .or(countMapping)
        );

        literal.set(
                term.then(
                        Reducer.opt(
                                t("?").then(builder.<AstNode>value(Type.Opt))
                                        .or(t("+").then(builder.value(Type.Plus)))
                                        .or(t("*").then(builder.value(Type.Rep)))
                                        .or(t("^").then(builder.value(Type.Eager)))
                                        .or(t("{").then(range).then(t("}"))
                                                .fold(builder.binary(Type.Range))
                                        )
                        )
                )
        );

        // term:'('expr ')'
        //        | TEXT
        //        | CHARSET
        //        | IDENTIFIER

        term.set(
                t("(").then(expr).then(t(")"))
                        .or(text)
                        .or(charSet)
                        .or(ref)
        );

    }

    private void initEscChars() {
        // SPECIAL CHARACTERS
        Regex hexDigit = CharSet.interval('0', '9', 'A', 'F', 'a', 'f');

        Regex escHexRex =
                Regex.text("\\u").then(hexDigit.count(4))
                        .or(Regex.text("\\U").then(hexDigit.count(8)))
                        .or(Regex.text("\\x").then(hexDigit.count(2)));

        Regex escCharRex =
                escHexRex
                        .or(Regex.text("\\").then(
                                Regex.text("n")
                                        .or(Regex.text("r"))
                                        .or(Regex.text("t"))
                                        .or(Regex.text("b"))
                                        .or(Regex.text("f"))
                                        .or(Regex.text("\\"))
                                        .or(Regex.text("]"))
                                        .or(Regex.text("-"))
                                        .or(Regex.text("\'"))
                                        .or(Regex.text("\""))
                        ));

        escChar.set(
                Parser.fromToken(rawLexer.token(escCharRex),
                        new EscChars(),
                        false)
        );
    }

    private void initText() {
        // TEXT
        Regex charRex = CharSet.chars('\'', '\\').invert();

        Parser<Integer> chr = Parser.fromToken(
                rawLexer.token(charRex),
                (stream, seq) -> (int) seq.charAt(0), false);

        Parser<Integer> chars = chr.or(escChar);

        Reducer<String, String> appendChar = chars.fold(
                (stream, left, right) -> left + new String(Character.toChars(right))
        );

        Initializer<String> emptyString = (stream) -> "";

        Parser<String> rawText = Recognizer.fromString("'", rawLexer, false)
                .then(emptyString)
                .then(Reducer.rep(appendChar))
                .then(Recognizer.fromString("'", rawLexer, false));

        text.set(rawText.then(builder.value(Type.Text)));
    }

    private void initCharSet() {
        // CHARSET: (also regex) ~[...] | [...] | . ;

        Regex normalSetStartCharsRex = CharSet.chars(']', '\\').invert();
        Regex normalSetEndCharsRex = CharSet.chars('\\').invert();

        Parser<Integer> setStartChars =
                Parser.fromToken(rawLexer.token(normalSetStartCharsRex),
                        (stream, seq) -> (int) seq.charAt(0), false)
                        .or(escChar);

        Parser<Integer> setEndChars =
                Parser.fromToken(rawLexer.token(normalSetEndCharsRex),
                        (stream, seq) -> (int) seq.charAt(0), false)
                        .or(escChar);

        Fold<Integer, Integer, CharSet> interval =
                (stream, left, right) -> CharSet.interval((int) left, right);
        Mapping<Integer, CharSet> singleChar = (stream, ch) -> CharSet.chars(ch);

        Parser<CharSet> simpleRange = setStartChars
                .then(
                        Recognizer.fromString("-", rawLexer, false)
                                .then(setEndChars.fold(interval))
                                .or(singleChar)
                );

        Initializer<CharSet> emptySet = (stream) -> CharSet.empty();

        Fold<CharSet, CharSet, CharSet> union = (stream, left, right) -> left.union(right);

        Parser<CharSet> ranges = emptySet.then(Reducer.rep(simpleRange.fold(union)));

        Parser<CharSet> rawNonInvCharSet =
                Recognizer.fromString("[", rawLexer, false)
                        .then(ranges)
                        .then(Recognizer.fromString("]", rawLexer, false)
                        );

        Mapping<CharSet, CharSet> invert = (stream, set) -> set.invert();

        Parser<CharSet> rawCharSet =
                Recognizer.fromString("~", rawLexer, false)
                        .then(rawNonInvCharSet)
                        .then(invert)
                        .or(rawNonInvCharSet);

        Parser<CharSet> allChars =
                Recognizer.fromString(".", lexer, false)
                        .then((Initializer<CharSet>) (stream) -> CharSet.all());

        charSet.set(
                rawCharSet.or(allChars).then(builder.value(Type.CharSet))
        );
    }

    private void initRef() {
        // JAVA CODE
        Regex regex = Regex.text("`").then(
                CharSet.chars('`').invert()
                        .or(Regex.text("``"))
                        .rep()
        ).then(Regex.text("`"));

        javaCode.set(
                Parser.fromToken(rawLexer.token(regex),
                        new JavaCode(),
                        false)
        );

        // IDENTIFIER
        Regex idRex =
                CharSet.interval('A', 'Z', 'a', 'z').or(CharSet.chars('_'))
                        .then(
                                CharSet.interval('A', 'Z', 'a', 'z', '0', '9')
                                        .or(CharSet.chars('_')).rep()
                        );

        identifier.set(Parser.fromToken(lexer.token(idRex), (stream, seq) -> seq.toString(), false));

        // REF
        ref.set(javaCode.or(identifier).then(builder.value(Type.Reference)));
    }

    /**
     * Grammar
     */

    public enum Type {
        Expr, Concat, Literal, Term, Text, Rule, Identifier, Choice, Opt, Plus, Rep, Eager, EscChar, Annotate, Fold, JavaRef, None, Range, Reference, JavaCode, CharSet
    }

    private static class JavaCode implements Mapping<CharSequence, String> {
        @NotNull
        @Override
        public String parse(ParserStream stream, @NotNull CharSequence left) {
            StringBuilder sb = new StringBuilder();

            for (int i = 1; i < left.length() - 1; ++i) {
                char ch = left.charAt(i);
                sb.append(ch);
                if (ch == '`') {
                    // skip char after `. It can only be `
                    i++;
                }
            }

            return sb.toString();
        }
    }
}

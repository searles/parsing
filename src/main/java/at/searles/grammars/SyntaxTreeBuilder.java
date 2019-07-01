package at.searles.grammars;

import at.searles.grammars.nodes.*;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.builder.AstNodeBuilder;
import at.searles.regex.Regex;

import java.util.List;
import java.util.Map;

public class SyntaxTreeBuilder implements AstNodeBuilder<ParserGenerator.Type> {
    @Override
    public <V, R> AstNode createBin(ParserStream stream, ParserGenerator.Type label, V left, R right) {
        switch(label) {
            case Rule:
                return new RuleNode(stream, (String) left, (ExprNode) right);
            case Choice:
            case Concat:
                return new BinNode(stream, label, (ExprNode) left, (ExprNode) right);
            case Range:
                return new RangeNode(stream, (ExprNode) left, (int[]) right);
            case Annotate:
                return new AnnotateNode(stream, (ExprNode) left, (String) right);
            case Fold:
                return new FoldNode(stream, (ExprNode) left, (String) right);
            default:
                throw new IllegalArgumentException(label.toString());
        }
    }

    @Override
    public <V> AstNode createValue(ParserStream stream, ParserGenerator.Type label, V value) {
        switch (label) {
            case Opt:
            case Plus:
            case Rep:
            case Eager:
                return new UnaryNode(stream, label, ((ExprNode) value));
            case Reference:
                return new ReferenceNode(stream, (String) value);
            case CharSet:
                return new RegexNode(stream, (Regex) value);
            case Text:
                return new RegexNode(stream, Regex.text((String) value));
            default:
                throw new IllegalArgumentException(label.toString());
        }
    }

    @Override
    public AstNode createItem(ParserStream stream, ParserGenerator.Type label) {
        switch (label) {
            case None:
                return new EmptyNode(stream);
            default:
                throw new IllegalArgumentException(label.toString());
        }
    }

    @Override
    public <V> AstNode createList(ParserStream stream, ParserGenerator.Type label, List<V> list) {
        throw new IllegalArgumentException(label.toString());
    }

    @Override
    public AstNode createToken(ParserStream stream, ParserGenerator.Type label, CharSequence left) {
        throw new IllegalArgumentException(label.toString());
    }

    @Override
    public <V> AstNode createMap(ParserStream stream, ParserGenerator.Type label, Map<ParserGenerator.Type, V> map) {
        throw new IllegalArgumentException(label.toString());
    }
}

package at.searles.grammars;

import at.searles.grammars.nodes.*;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.SourceInfo;
import at.searles.parsing.utils.ast.builder.AstNodeBuilder;
import at.searles.regex.Regex;

import java.util.List;
import java.util.Map;

public class SyntaxTreeBuilder implements AstNodeBuilder<ParserGenerator.Type> {
    @Override
    public <V, R> AstNode createBin(SourceInfo info, ParserGenerator.Type label, V left, R right) {
        switch(label) {
            case Rule:
                return new RuleNode(info, (String) left, (ExprNode) right);
            case Choice:
            case Concat:
                return new BinNode(info, label, (ExprNode) left, (ExprNode) right);
            case Range:
                return new RangeNode(info, (ExprNode) left, (int[]) right);
            case Annotate:
                return new AnnotateNode(info, (ExprNode) left, (String) right);
            case Fold:
                return new FoldNode(info, (ExprNode) left, (String) right);
            default:
                throw new IllegalArgumentException(label.toString());
        }
    }

    @Override
    public <V> AstNode createValue(SourceInfo info, ParserGenerator.Type label, V value) {
        switch (label) {
            case Opt:
            case Plus:
            case Rep:
            case Eager:
                return new UnaryNode(info, label, ((ExprNode) value));
            case Reference:
                return new ReferenceNode(info, (String) value);
            case CharSet:
                return new RegexNode(info, (Regex) value);
            case Text:
                return new RegexNode(info, Regex.text((String) value));
            default:
                throw new IllegalArgumentException(label.toString());
        }
    }

    @Override
    public AstNode createItem(SourceInfo info, ParserGenerator.Type label) {
        switch (label) {
            case None:
                return new EmptyNode(info);
            default:
                throw new IllegalArgumentException(label.toString());
        }
    }

    @Override
    public <V> AstNode createList(SourceInfo info, ParserGenerator.Type label, List<V> list) {
        throw new IllegalArgumentException(label.toString());
    }

    @Override
    public AstNode createToken(SourceInfo info, ParserGenerator.Type label, CharSequence left) {
        throw new IllegalArgumentException(label.toString());
    }

    @Override
    public <V> AstNode createMap(SourceInfo info, ParserGenerator.Type label, Map<ParserGenerator.Type, V> map) {
        throw new IllegalArgumentException(label.toString());
    }
}

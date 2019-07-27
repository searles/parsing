package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Fold;
import at.searles.parsing.Initializer;
import at.searles.parsing.Mapping;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.SourceInfo;

import java.util.List;
import java.util.Map;

/**
 * This builder interface allows to create and also to invert
 * abstract syntax tree nodes. For this purpose, it provides various builder
 * methods (createXXX) and their (partly and not necessarily total) inverts
 * (matchXXX).
 *
 * @param <L> A label that indicates the type of the node.
 */
public interface AstNodeBuilder<L> {
    <V, R> AstNode createBin(SourceInfo info, L label, V left, R right);

    default <V> V matchLeft(L label) {
        throw new UnsupportedOperationException();
    }

    default <R> R matchRight(L label) {
        throw new UnsupportedOperationException();
    }

    <V> AstNode createValue(SourceInfo info, L label, V value);

    default <V> V matchValue(L label, AstNode node) {
        throw new UnsupportedOperationException();
    }

    <V> AstNode createList(SourceInfo info, L label, List<V> list);

    default <V> List<V> matchList(AstNode result) {
        throw new UnsupportedOperationException();
    }

    AstNode createToken(SourceInfo info, L label, CharSequence left);

    default CharSequence matchToken(L label, AstNode node) {
        throw new UnsupportedOperationException();
    }

    AstNode createItem(SourceInfo info, L label);

    default boolean matchItem(L label) {
        throw new UnsupportedOperationException();
    }

    <V> AstNode createMap(SourceInfo info, L label, Map<L, V> map);

    default <V> Map<L, V> matchMap(L label, AstNode node) {
        throw new UnsupportedOperationException();
    }

    default <R, V> Fold<R, V, AstNode> binary(L label) {
        return new BinaryBuilder<>(label, this);
    }

    default Initializer<AstNode> empty(L label) {
        return new EmptyBuilder<>(label, this);
    }

    default <V> Mapping<V, AstNode> value(L label) {
        return new UnaryBuilder<>(label, this);
    }

    default <V> Mapping<Map<L, V>, AstNode> map(L label) {
        return new MapBuilder<>(label, this);
    }
}

package at.searles.parsing;

import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Mapping<T, U> extends Reducer<T, U> {

    @Override
    U parse(Environment env, @NotNull T left, ParserStream stream);

    default @Nullable
    T left(Environment env, @NotNull U result) {
        throw new UnsupportedOperationException();
    }

    default PartialConcreteSyntaxTree<T> print(Environment env, @NotNull U u) {
        T left = left(env, u);

        if(left == null) {
            return null;
        }

        return new PartialConcreteSyntaxTree<>(left, ConcreteSyntaxTree.empty());
    }

    @Override
    default boolean recognize(Environment env, ParserStream stream) {
        return true;
    }
}
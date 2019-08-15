package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Mapping<T, U> extends Reducer<T, U> {

    @Override
    U parse(ParserStream stream, @NotNull T left);

    default @Nullable
    T left(@NotNull U result) {
        throw new UnsupportedOperationException();
    }

    @Override
    default PartialConcreteSyntaxTree<T> print(@NotNull U u) {
        T left = left(u);

        if (left == null) {
            return null;
        }

        return new PartialConcreteSyntaxTree<>(left, ConcreteSyntaxTree.empty());
    }

    @Override
    default boolean recognize(ParserStream stream) {
        return true;
    }
}
package at.searles.parsing;

import at.searles.parsing.printing.PartialStringTree;
import at.searles.parsing.printing.StringTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Mapping<T, U> extends Reducer<T, U> {

    @Override
    @NotNull
    U parse(Environment env, T left, ParserStream stream);

    default @Nullable
    T left(Environment env, U result) {
        throw new UnsupportedOperationException();
    }

    default PartialStringTree<T> print(Environment env, U u) {
        T left = left(env, u);

        if(left == null) {
            return null;
        }

        return new PartialStringTree<>(left, StringTree.empty());
    }

    @Override
    default boolean recognize(Environment env, ParserStream stream) {
        return true;
    }
}
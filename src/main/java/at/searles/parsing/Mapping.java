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
        return null;
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

    static <T> Mapping<T, T> identity() {
        return IdentityHolder.getInstance();
    }

    class IdentityHolder {
        private static final Mapping<?, ?> INSTANCE = new Mapping<Object, Object>() {
            @Override
            public Object parse(ParserStream stream, @NotNull Object left) {
                return left;
            }

            @Override
            public Object left(@NotNull Object result) {
                return result;
            }

            @Override
            public String toString() {
                return "{identity}";
            }
        };

        private static <T> Mapping<T,T> getInstance() {
            //noinspection unchecked
            return (Mapping<T, T>) INSTANCE;
        }
    }
}
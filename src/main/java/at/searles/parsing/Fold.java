package at.searles.parsing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Fold<T, U, V> {
    V apply(ParserStream stream, @NotNull T left, @NotNull U right); // must not return null

    default @Nullable
    T leftInverse(@NotNull V result) {
        return null;
    }

    default @Nullable
    U rightInverse(@NotNull V result) {
        return null;
    }
}
package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Cast<T, U> implements Mapping<T, U> {
    private final Class<T> srcType;
    private final Class<U> dstType;

    public Cast(Class<T> srcType, Class<U> dstType) {
        this.srcType = srcType;

        this.dstType = dstType;
    }

    @Override
    public U parse(Environment env, @NotNull T left, ParserStream stream) {
        if(!dstType.isInstance(left)) {
            return null;
        }

        return dstType.cast(left);
    }

    @Nullable
    @Override
    public T left(Environment env, @NotNull U result) {
        if(!srcType.isInstance(result)) {
            return null;
        }

        return srcType.cast(result);
    }
}

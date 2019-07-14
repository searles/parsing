package at.searles.parsing.utils.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Apply<T, U> implements Mapping<T, U> {
    private final Method applyMethod;
    private final Method builderCreate;
    private final Class<U> itemType;
    private final Class<T> builderType;

    public Apply(Class<T> builderType, Class<U> itemType) {
        this.itemType = itemType;
        this.builderType = builderType;

        try {
            this.applyMethod = builderType.getMethod("apply", ParserStream.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }

        Method method;

        try {
            // printing is optional.
            method = builderType.getMethod("create", itemType);
        } catch (NoSuchMethodException e) {
            method = null;
        }

        this.builderCreate = method;
    }

    @Override
    public U parse(Environment env, @NotNull T left, ParserStream stream) {
        try {
            return itemType.cast(this.applyMethod.invoke(left, stream));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public T left(Environment env, @NotNull U result) {
        try {
            return builderType.cast(builderCreate.invoke(null, result));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{apply}";
    }
}

package at.searles.parsing.utils.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BuildUnsafe<T, U> implements Mapping<T, U> {

    private final Method buildMethod;
    private final Method builderCreate;

    public BuildUnsafe(Class<T> builderType) {
        try {
            this.buildMethod = builderType.getMethod("build", Environment.class, ParserStream.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }

        Method method;

        try {
            // printing is optional.
            method = builderType.getMethod("toBuilder", buildMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            method = null;
        }

        this.builderCreate = method;
    }

    @Override
    public U parse(Environment env, ParserStream stream, @NotNull T left) {
        try {
            return (U) buildMethod.invoke(left, env, stream);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public T left(Environment env, @NotNull U result) {
        if(!buildMethod.getReturnType().isInstance(result)) {
            return null;
        }

        try {
            return (T) builderCreate.invoke(null, result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{build}";
    }
}

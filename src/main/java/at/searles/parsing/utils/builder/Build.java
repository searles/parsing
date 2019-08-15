package at.searles.parsing.utils.builder;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Build<T, U> implements Mapping<T, U> {
    private final Method applyMethod;
    private final Method builderCreate;
    private final Class<U> itemType;
    private final Class<T> builderType;

    public Build(Class<T> builderType, Class<U> itemType) {
        this.itemType = itemType;
        this.builderType = builderType;

        try {
            this.applyMethod = builderType.getMethod("build", ParserCallBack.class, ParserStream.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }

        Method method;

        try {
            // printing is optional.
            method = builderType.getMethod("toBuilder", itemType);
        } catch (NoSuchMethodException e) {
            method = null;
        }

        this.builderCreate = method;
    }

    @Override
    public U parse(ParserCallBack env, ParserStream stream, @NotNull T left) {
        try {
            return itemType.cast(this.applyMethod.invoke(left, env, stream));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public T left(PrinterCallBack env, @NotNull U result) {
        try {
            return builderType.cast(builderCreate.invoke(null, result));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{build}";
    }
}

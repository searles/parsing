package at.searles.parsing.utils.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Apply<T, U> implements Mapping<T, U> {
    private final Method applyMethod;
    private final Constructor<T> builderCtor;
    private final Class<U> itemType;

    public Apply(Class<T> builderType, Class<U> itemType) {
        this.itemType = itemType;

        try {
            this.applyMethod = builderType.getMethod("apply");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }

        Constructor<T> builderCtor;

        try {
            builderCtor = builderType.getConstructor(itemType);
        } catch (NoSuchMethodException e) {
            builderCtor = null;
        }

        this.builderCtor = builderCtor;
    }

    @Override
    public U parse(Environment env, @NotNull T left, ParserStream stream) {
        try {
            return itemType.cast(this.applyMethod.invoke(left));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public T left(Environment env, @NotNull U result) {
        try {
            return builderCtor.newInstance(result);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{apply}";
    }
}

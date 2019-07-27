package at.searles.parsing.utils.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Setter<T, V> implements Fold<T, V, T> {

    private final Class<T> type;
    private final Class<V> parameterType;

    private final Method copyMethod;

    private final String property;
    private final Field field;

    public Setter(Class<T> type, String property, Class<V> parameterType) {
        try {
            this.type = type;
            this.parameterType = parameterType;
            this.field = type.getField(property);
            this.copyMethod = type.getMethod("copy");
            this.property = property;
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public T apply(Environment env, ParserStream stream, @NotNull T left, @NotNull V right) {
        try {
            // clone to allow backtracking
            T copy = type.cast(copyMethod.invoke(left));
            field.set(copy, right);
            return copy;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public T leftInverse(Environment env, @NotNull T result) {
        try {
            Object value = field.get(result);

            if (value == null) {
                return null;
            }

            T copy = type.cast(copyMethod.invoke(result));

            field.set(copy, null);

            return copy;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public V rightInverse(Environment env, @NotNull T result) {
        try {
            Object value = field.get(result);

            if (value == null) {
                return null;
            }

            return parameterType.cast(value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{set " + property + "}";
    }
}

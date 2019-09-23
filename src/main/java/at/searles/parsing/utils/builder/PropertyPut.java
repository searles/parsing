package at.searles.parsing.utils.builder;

import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyPut<T> implements Fold<Properties, T, Properties> {

    private final String propertyId;

    public PropertyPut(String propertyId) {
        this.propertyId = propertyId;
    }

    @Override
    public Properties apply(ParserStream stream, @NotNull Properties left, @NotNull T right) {
        return left.concat(propertyId, right);
    }

    @Override
    public Properties leftInverse(@NotNull Properties result) {
        return result.diff(propertyId);
    }

    @Override
    public T rightInverse(@NotNull Properties result) {
        return result.get(propertyId);
    }

    @Override
    public String toString() {
        return "{put " + propertyId + "}";
    }
}

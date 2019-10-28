package at.searles.parsing.utils.builder;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This one is for objects with getters and constructors in which
 * all elements are set.
 */
public class Creator<T> implements Mapping<Properties, T> {

    private final Constructor<? extends T> ctor;
    private final Map<String, Method> getters;
    private final String[] properties;
    private final boolean withInfo;
    private final Class<? extends T> clazz;

    public Creator(Class<? extends T> clazz, boolean withInfo, String...properties) {
        this.clazz = clazz;
        this.withInfo = withInfo;
        this.properties = properties;
        getters = new HashMap<>();
        Class<?>[] parameterTypes = new Class<?>[properties.length + (withInfo ? 1 : 0)];

        try {
            int index = 0;

            if(withInfo) {
                parameterTypes[index++] = SourceInfo.class;
            }

            for(String property : properties) {
                Method getter = MethodUtils.getter(clazz, property);
                getters.put(property, getter);
                parameterTypes[index++] = getter.getReturnType();
            }

            this.ctor = clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Creator(Class<? extends T> clazz, String...properties) {
        this(clazz, false, properties);
    }

    @Override
    public T parse(ParserStream stream, @NotNull Properties left) {
        Object[] arguments = new Object[properties.length  + (withInfo ? 1 : 0)];
        int index = 0;

        if(withInfo) {
            arguments[index++] = stream.createSourceInfo();
        }

        for(String property: properties) {
            arguments[index++] = left.get(property);
        }

        try {
            return ctor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public Properties left(@NotNull T result) {
        if(!clazz.isInstance(result)) {
            return null;
        }

        try {
            HashMap<String, Object> map = new HashMap<>();

            for(String property: properties) {
                map.put(property, getters.get(property).invoke(result));
            }

            return new Properties(map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{create}";
    }
}

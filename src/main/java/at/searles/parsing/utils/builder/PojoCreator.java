package at.searles.parsing.utils.builder;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Creates an object with setters and getters but without knowing
 * the parameters. No inversion thus.
 */
public class PojoCreator<T> implements Mapping<Properties, T> {

    private Constructor<T> ctor;
    private Class<T> clazz;

    public PojoCreator(Class<T> clazz, boolean withInfo) {
        try {
            this.ctor = withInfo ? clazz.getConstructor(SourceInfo.class) : clazz.getConstructor();
            this.clazz = clazz;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public PojoCreator(Class<T> clazz) {
        this(clazz, false);
    }

    @Override
    public T parse(ParserStream stream, @NotNull Properties left) {
        try {
            T obj = ctor.getParameterCount() == 0 ? ctor.newInstance() : ctor.newInstance(stream.createSourceInfo());

            for(String key: left) {
                Method setter = MethodUtils.setter(clazz, key);
                setter.invoke(left.get(key));
            }

            return obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

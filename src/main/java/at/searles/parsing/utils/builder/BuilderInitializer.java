package at.searles.parsing.utils.builder;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BuilderInitializer<T> implements Initializer<T> {

    private final Method isEmptyMethod;
    private final Constructor<T> ctor;

    public BuilderInitializer(Class<T> cls) {
        try {
            this.ctor = cls.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }

        Method isEmptyMethod;

        try {
            isEmptyMethod = cls.getMethod("isEmpty");
        } catch (NoSuchMethodException e) {
            isEmptyMethod = null;
        }

        this.isEmptyMethod = isEmptyMethod;
    }

    @Override
    public T parse(ParserCallBack env, ParserStream stream) {
        try {
            return ctor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean consume(PrinterCallBack env, T builder) {
        try {
            return (boolean) isEmptyMethod.invoke(builder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return "{builder}";
    }
}

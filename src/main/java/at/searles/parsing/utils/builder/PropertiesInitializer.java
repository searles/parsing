package at.searles.parsing.utils.builder;

import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertiesInitializer implements Initializer<Properties> {

    private static class Holder {
        static Properties instance = new Properties();
    }

    @Override
    public Properties parse(ParserStream stream) {
        return Holder.instance;
    }

    @Override
    public boolean consume(Properties properties) {
        return properties.isEmpty();
    }

    @Override
    public String toString() {
        return "{empty properties}";
    }
}
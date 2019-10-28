package at.searles.parsing.utils.builder;

import java.lang.reflect.Method;

public class MethodUtils {
    private static String methodAccess(String prefix, String property) {
        return prefix + property.substring(0, 1).toUpperCase() + property.substring(1);
    }

    public static Method setter(Class<?> clazz, String propertyName) throws NoSuchMethodException {
        return clazz.getMethod(methodAccess("set", propertyName), getter(clazz, propertyName).getReturnType());
    }

    public static Method getter(Class<?> clazz, String propertyName) throws NoSuchMethodException {
        return clazz.getMethod(methodAccess("get", propertyName));
    }
}

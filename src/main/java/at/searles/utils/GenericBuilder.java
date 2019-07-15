package at.searles.utils;

import java.lang.reflect.Field;

public class GenericBuilder<A extends GenericBuilder<A>> implements Cloneable {
    public A copy() {
        try {
            return (A) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean isEmpty() {
        try {
            for(Field field: getClass().getFields()) {
                if(field.get(this) != null) {
                    return false;
                }
            }

            return true;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

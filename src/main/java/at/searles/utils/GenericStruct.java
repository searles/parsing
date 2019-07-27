package at.searles.utils;

import java.lang.reflect.Field;

/**
 * A generic data object contains public (!) objects. The names of these member
 * variables are set using Utils.setter. All these members are considered to
 * be properties that can be set. If they contain null, they are not
 * set.
 *
 * In order to also use Utils.build, there must be a method
 * <code>B build(Environment env, ParserStream stream)</code> that returns the
 * built object. For inversion, also create a static method
 * <code>public static A toBuilder(B object)</code>
 * that creates a builder out of an object. If it does not exist, inversion
 * of the Utils.build()-method will fail.
 *
 * @param <A> The concrete builder class
 */
public abstract class GenericStruct<A extends GenericStruct<A>> implements Cloneable {

    public A copy() {
        try {
            //noinspection unchecked
            return (A) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean isEmpty() {
        try {
            // XXX Another option would be annotations.
            for (Field field : getClass().getFields()) {
                if (field.get(this) != null) {
                    return false;
                }
            }

            return true;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

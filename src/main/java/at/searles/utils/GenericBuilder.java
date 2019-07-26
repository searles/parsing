package at.searles.utils;

import at.searles.parsing.Environment;
import at.searles.parsing.ParserStream;

import java.lang.reflect.Field;

/**
 * A generic builder contains public (!) objects. The names of these member
 * variables are set using Utils.setter. All these members are considered to
 * be properties that can be set. If they contain null, they are not
 * set. For inversion, there must be a static method
 *
 * public static A toBuilder(B object)
 *
 * that creates a builder out of an object. If it does not exist, inversion
 * of the Utils.build()-method will fail.
 *
 * @param <A> The concrete builder class
 * @param <B> The item class that is built.
 */
public abstract class GenericBuilder<A extends GenericBuilder<A, B>, B> implements Cloneable {

    public abstract B build(Environment env, ParserStream stream);

    public A copy() {
        try {
            return (A) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean isEmpty() {
        try {
            // XXX Another option would be annotations.
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

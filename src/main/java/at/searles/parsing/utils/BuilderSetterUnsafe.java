package at.searles.parsing.utils;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.builder.BuilderInitializer;
import at.searles.parsing.utils.builder.Setter;
import at.searles.parsing.utils.builder.SetterUnsafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuilderSetterUnsafe<T, V> implements Mapping<V, T> {
    private final String property;
    private final SetterUnsafe<T, V> setterUnsafe;
    private final BuilderInitializer<T> builderInitializer;

    public BuilderSetterUnsafe(Class<T> builder, String property) {
        builderInitializer = new BuilderInitializer<>(builder);
        setterUnsafe = new SetterUnsafe<>(property);
        this.property = property;
    }

    @Override
    public T parse(Environment env, @NotNull V left, ParserStream stream) {
        T builder = builderInitializer.parse(env, stream);
        return setterUnsafe.apply(env, builder, left, stream);
    }

    @Nullable
    @Override
    public V left(Environment env, @NotNull T result) {
        V left = setterUnsafe.rightInverse(env, result);

        if(left == null) {
            return null;
        }

        T leftBuilder = setterUnsafe.leftInverse(env, result);

        if(leftBuilder == null || !builderInitializer.consume(env, leftBuilder)) {
            return null;
        }

        return left;
    }

    @Override
    public String toString() {
        return String.format("{setter %s}", property);
    }
}

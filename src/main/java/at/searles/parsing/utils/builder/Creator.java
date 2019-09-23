package at.searles.parsing.utils.builder;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Creator<T> implements Mapping<Properties, T> {

    private final GenFactory<T> factory;

    public Creator(Class<T> clazz, String...properties) {
        this.factory = new GenFactory<>(clazz, properties);
    }

    @Override
    public T parse(ParserStream stream, @NotNull Properties left) {
        return factory.fromProperties(stream, left);
    }

    @Nullable
    @Override
    public Properties left(@NotNull T result) {
        return factory.toProperties(result);
    }

    @Override
    public String toString() {
        return "{create}";
    }
}

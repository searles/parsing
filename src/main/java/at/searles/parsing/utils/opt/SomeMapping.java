package at.searles.parsing.utils.opt;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by searles on 02.04.19.
 */
public class SomeMapping<T> implements Mapping<T, Optional<T>> {
    @Override
    public Optional<T> parse(Environment env, @NotNull T left, ParserStream stream) {
        return Optional.of(left);
    }

    @Override
    public T left(Environment env, Optional<T> result) {
        return result.orElse(null);
    }

    @Override
    public String toString() {
        return "{some}";
    }
}

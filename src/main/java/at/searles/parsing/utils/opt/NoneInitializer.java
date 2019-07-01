package at.searles.parsing.utils.opt;

import at.searles.parsing.Environment;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;

import java.util.Optional;

/**
 * Created by searles on 02.04.19.
 */
public class NoneInitializer<T> implements Initializer<Optional<T>> {
    @Override
    public Optional<T> parse(Environment env, ParserStream stream) {
        return Optional.empty();
    }

    @Override
    public boolean consume(Environment env, Optional<T> t) {
        return !t.isPresent();
    }

    @Override
    public String toString() {
        return "{none}";
    }
}

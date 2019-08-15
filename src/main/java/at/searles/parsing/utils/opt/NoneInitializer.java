package at.searles.parsing.utils.opt;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;

import java.util.Optional;

/**
 * Created by searles on 02.04.19.
 */
public class NoneInitializer<T> implements Initializer<Optional<T>> {
    @Override
    public Optional<T> parse(ParserCallBack env, ParserStream stream) {
        return Optional.empty();
    }

    @Override
    public boolean consume(PrinterCallBack env, Optional<T> t) {
        return !t.isPresent();
    }

    @Override
    public String toString() {
        return "{none}";
    }
}

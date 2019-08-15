package at.searles.parsing.utils.list;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by searles on 31.03.19.
 */
public class SingleList<T> implements Mapping<T, List<T>> {

    @Override
    public List<T> parse(ParserStream stream, @NotNull T left) {
        List<T> l = new ArrayList<>();
        l.add(left);
        return l;
    }

    @Override
    public T left(@NotNull List<T> result) {
        return result.size() == 1 ? result.get(0) : null;
    }

    @Override
    public String toString() {
        return "{list(x)}";
    }
}

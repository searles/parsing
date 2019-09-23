package at.searles.parsing.utils.builder;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PropertiesSingleton<T> implements Mapping<T, Properties> {
    private final String id;

    public PropertiesSingleton(String id) {
        this.id = id;
    }

    @Override
    public Properties parse(ParserStream stream, @NotNull T left) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(id, left);
        return new Properties(map);
    }

    @Nullable
    @Override
    public T left(@NotNull Properties result) {
        if(result.size() != 1) {
            return null;
        }

        return (T) result.get(id);
    }
}

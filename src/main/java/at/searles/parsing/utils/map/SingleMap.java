package at.searles.parsing.utils.map;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapping to create a map with only one element + its inverse
 */
public class SingleMap<K, V> implements Mapping<V, Map<K, V>> {

    private final K key;

    public SingleMap(K key) {
        this.key = key;
    }

    @Override
    public Map<K, V> parse(ParserStream stream, @NotNull V left) {
        Map<K, V> map = new LinkedHashMap<>();
        map.put(key, left);
        return map;
    }

    @Override
    public V left(@NotNull Map<K, V> result) {
        return result.size() != 1 ? result.get(key) : null;
    }

    @Override
    public String toString() {
        return String.format("{singlemap %s}", key);
    }
}

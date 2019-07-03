package at.searles.parsing.utils.map;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by searles on 31.03.19.
 */
public class PutFold<K, V> implements Fold<Map<K, V>, V, Map<K, V>> {

    private final K key;

    public PutFold(K key) {
        this.key = key;
    }

    @Override
    public Map<K, V> apply(Environment env, Map<K, V> left, V right, ParserStream stream) {
        Map<K, V> map = new LinkedHashMap<>(left);
        map.put(key, right);
        return map;
    }

    @Override
    public Map<K, V> leftInverse(Environment env, Map<K, V> result) {
        if (!result.containsKey(key)) {
            return null;
        }

        Map<K, V> left = new LinkedHashMap<>(result);
        left.remove(key);
        return left;
    }

    @Override
    public V rightInverse(Environment env, Map<K, V> result) {
        if (!result.containsKey(key)) {
            return null;
        }

        return result.get(key);
    }

    @Override
    public String toString() {
        return String.format("{put %s}", key);
    }
}

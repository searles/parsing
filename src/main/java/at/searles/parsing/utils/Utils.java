package at.searles.parsing.utils;

import at.searles.parsing.*;
import at.searles.parsing.utils.common.PairFold;
import at.searles.parsing.utils.common.ValueInitializer;
import at.searles.parsing.utils.list.BinaryList;
import at.searles.parsing.utils.list.ConsFold;
import at.searles.parsing.utils.list.EmptyList;
import at.searles.parsing.utils.list.SingleList;
import at.searles.parsing.utils.map.EmptyMap;
import at.searles.parsing.utils.map.PutFold;
import at.searles.parsing.utils.map.SingleMap;
import at.searles.parsing.utils.opt.NoneInitializer;
import at.searles.parsing.utils.opt.SomeMapping;
import at.searles.utils.Pair;

import java.util.*;

public class Utils {

    /**
     * Parser for a separated list.
     * @param <T> The base type.
     * @param separator The separator, eg a comma
     * @param parser The parser for all elements
     * @return An inversible parser for a list of items.
     */
    public static <T> Parser<List<T>> csv1(Recognizer separator, Parser<T> parser) {
         return list(parser)
             .then(Reducer.rep(separator.then(cons(false, parser))));
    }
    
    public static <T> Parser<List<T>> csv(Recognizer separator, Parser<T> parser) {
        return Utils.<T>list().then(
                Reducer.opt(
                        cons(true, parser)
                        .then(Reducer.rep(separator.then(cons(false, parser))))
                )
        );
    }

    public static <T> Parser<List<T>> rep1(Parser<T> parser) {
        return list(parser).then(Reducer.rep(cons(false, parser)));
    }

    public static <T> Parser<List<T>> rep(Parser<T> parser) {
        return Utils.<T>list()
                .then(Reducer.opt(
                        cons(true, parser)
                        .then(Reducer.rep(cons(false, parser)))
                )
        );
    }

    public static <T> Parser<Optional<T>> opt(Parser<T> parser) {
        return parser.then(new SomeMapping<>()).or(new NoneInitializer<>());
    }

    public static <T, U> Reducer<T, Pair<T, U>> pair(Parser<U> rightParser) {
        return rightParser.fold(new PairFold<>());
    }

    public static <T> Initializer<List<T>> list() {
        return new EmptyList<>();
    }

    public static <T> Parser<List<T>> list(Parser<T> parser) {
        return parser.then(new SingleList<>());
    }

    public static <T> Reducer<T, List<T>> list2(Parser<T> rightParser) {
        return rightParser.fold(new BinaryList<>());
    }

    public static <T> Reducer<List<T>, List<T>> cons(boolean leftMayBeEmpty, Parser<T> parser) {
        return parser.fold(new ConsFold<>(leftMayBeEmpty));
    }

    public static <V> Initializer<V> val(V v) {
        return new ValueInitializer<>(v);
    }

    public static <K, V> Initializer<Map<K, V>> map() {
        return new EmptyMap<>();
    }

    public static <K, V> Parser<Map<K, V>> map(K key, Parser<V> parser) {
        return parser.then(new SingleMap<>(key));
    }

    public static <K, V> Reducer<Map<K, V>, Map<K, V>> put(K key, Parser<V> itemParser) {
        return itemParser.fold(new PutFold<>(key));
    }
}

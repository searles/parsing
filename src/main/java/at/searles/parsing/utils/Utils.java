package at.searles.parsing.utils;

import at.searles.parsing.*;
import at.searles.parsing.utils.common.PairFold;
import at.searles.parsing.utils.common.SwapPairFold;
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

/**
 * This class contains utilities to create lists out of parsers for convenience.
 */
public class Utils {

    /**
     * Parser for a separated list.
     * @param <T> The base type.
     * @param separator The separator, eg a comma
     * @param parser The parser for all elements
     * @return An inversible parser for a list of items.
     */
    public static <T> Parser<List<T>> list1(Parser<T> parser, Recognizer separator) {
         return singleton(parser).then(Reducer.rep(separator.then(cons(parser, 1))));
    }

    public static <T> Parser<List<T>> list1(Parser<T> parser) {
        return singleton(parser).then(Reducer.rep(cons(parser, 1)));
    }

    public static <T> Parser<List<T>> list(Parser<T> parser, Recognizer separator) {
        return Utils.<T>empty().then(
                Reducer.opt(
                        cons(parser, 0)
                        .then(Reducer.rep(separator.then(cons(parser, 1))))
                )
        );
    }

    public static <T> Parser<List<T>> list(Parser<T> parser) {
        return Utils.<T>empty()
                .then(Reducer.opt(
                        cons(parser, 0)
                        .then(Reducer.rep(cons(parser, 1)))
                )
        );
    }

    public static <T> Parser<Optional<T>> opt(Parser<T> parser) {
        return parser.then(new SomeMapping<>()).or(new NoneInitializer<>());
    }

    public static <T, U> Reducer<T, Pair<T, U>> pair(Parser<U> rightParser) {
        return rightParser.fold(new PairFold<>());
    }

    public static <T, U> Reducer<T, Pair<U, T>> swapPair(Parser<U> leftParser) {
        return leftParser.fold(new SwapPairFold<>());
    }

    public static <T> Initializer<List<T>> empty() {
        return new EmptyList<>();
    }

    public static <T> Parser<List<T>> singleton(Parser<T> parser) {
        return parser.then(new SingleList<>());
    }

    public static <T> Reducer<T, List<T>> binary(Parser<T> rightParser) {
        return rightParser.fold(new BinaryList<>());
    }

    /**
     * Creates a reducer that appends a parsed element to the left list
     * @param minLeftElements The minimum number of elements that are asserted to be in the left list. This is
     *                        needed for inversion.
     */
    public static <T> Reducer<List<T>, List<T>> cons(Parser<T> parser, int minLeftElements) {
        return parser.fold(new ConsFold<>(minLeftElements));
    }

    /**
     * @return An initializer for a simple value.
     */
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

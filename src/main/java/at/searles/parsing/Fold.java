package at.searles.parsing;

public interface Fold<T, U, V> {
    V apply(Environment env, T left, U right, ParserStream stream); // must not return null

    default T leftInverse(Environment env, V result) {
        throw new UnsupportedOperationException();
    }

    default U rightInverse(Environment env, V result) {
        throw new UnsupportedOperationException();
    }

    default Mapping<U, V> toMapping(T left) {
        return new FoldToMapping<T, U, V>(left, this);
    }

    class FoldToMapping<T, U, V> implements Mapping<U, V> {

        private final T left;
        private final Fold<T, U, V> fold;

        FoldToMapping(T left, Fold<T, U, V> fold) {
            this.left = left;
            this.fold = fold;
        }

        @Override
        public V parse(Environment env, U mid, ParserStream stream) {
            return fold.apply(env, left, mid, stream);
        }

        @Override
        public U left(Environment env, V result) {
            T t = fold.leftInverse(env, result);

            if(!t.equals(left)) {
                return null;
            }

            return fold.rightInverse(env, result);
        }

        @Override
        public String toString() {
            return String.format("{[%s], %s}", left, fold);
        }
    }
}
package at.searles.parsing;

public class PossiblyInfiniteRecursionException extends RuntimeException {
    public PossiblyInfiniteRecursionException(Ref<?> source, StackOverflowError e) {
        super("Possibly infinite recursion in " + source.toString(), e);
    }
}

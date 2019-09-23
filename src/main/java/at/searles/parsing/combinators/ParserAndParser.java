package at.searles.parsing.combinators;

import at.searles.parsing.Fold;
import at.searles.parsing.Parser;
import at.searles.parsing.ParserStream;
import at.searles.parsing.printing.ConcreteSyntaxTree;

/**
 * Special parser to try two possibilities. If both apply, the fold-function
 * is applied. Both parser, if successful must end at the same position,
 * and the stream must support backtracking.
 */
public class ParserAndParser<T> implements Parser<T> {

    private final Parser<T> p1;
    private final Parser<T> p2;
    private final Fold<T, T, T> combinator;

    public ParserAndParser(Parser<T> p1, Parser<T> p2, Fold<T, T, T> combinator) {
        this.p1 = p1;
        this.p2 = p2;
        this.combinator = combinator;
    }

    @Override
    public T parse(ParserStream stream) {
        long preStart = stream.start();
        long preEnd = stream.end();

        T ret1 = p1.parse(stream);

        long ret1Start = stream.start();
        long ret1End = stream.end();

        if(ret1 != null) {
            stream.setOffset(preEnd);

            stream.setStart(preStart);
            stream.setEnd(preEnd);
        }

        T ret2 = p2.parse(stream);

        if(ret1 != null && ret2 != null) {
            if(ret1End != stream.end()) {
                throw new IllegalStateException("Both parsers were successful but ended at different positions");
            }

            return combinator.apply(stream, ret1, ret2);
        }

        if(ret1 != null) {
            // restore state of ret1
            stream.setOffset(ret1End);

            stream.setStart(ret1Start);
            stream.setEnd(ret1End);

            return ret1;
        }

        // only ret2 was successful or not.
        return ret2;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        // like or.
        return p1.recognize(stream) || p2.recognize(stream);
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        // combinator must decide which branch to take, otherwise branch 1 is used.
        T left = combinator.leftInverse(t);
        T right = combinator.rightInverse(t);

        if(left != null) {
            ConcreteSyntaxTree output = p1.print(t);

            if(output != null) {
                return output;
            }
        }

        if(right == null) {
            return null;
        }

        return p2.print(right);
    }

    @Override
    public String toString() {
        return String.format("(%s & %s) >> %s", p1, p2, combinator);
    }
}

package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import at.searles.parsing.printing.ConcreteSyntaxTree;

/**
 * Parser for chaining parsers. Eg for 5 + 6 where + 6 is the reducer.
 */
public class ParserThenReducer<T, U> implements Parser<U>, Recognizable.Then {

    private final Parser<T> parent;
    private final Reducer<T, U> reducer;

    public ParserThenReducer(Parser<T> parent, Reducer<T, U> reducer) {
        this.parent = parent;
        this.reducer = reducer;
    }

    @Override
    public U parse(Environment env, ParserStream stream) {
        long offset = stream.offset();

        // to restore if backtracking
        long preStart = stream.start();
        long preEnd = stream.end();

        T t = parent.parse(env, stream);
        
        if(t == null) {
            return null;
        }

        // reducer preserves start() in stream and only sets end().
        U u = reducer.parse(env, stream, t);

        if(u == null) {
            env.notifyNoMatch(stream, this);
            stream.setOffset(offset);
            stream.setStart(preStart);
            stream.setEnd(preEnd);

            return null;
        }

        return u;
    }

    @Override
    public Recognizable left() {
        return parent;
    }

    @Override
    public Recognizable right() {
        return reducer;
    }

    @Override
    public ConcreteSyntaxTree print(Environment env, U u) {
        PartialConcreteSyntaxTree<T> reducerOutput = reducer.print(env, u);

        if(reducerOutput == null) {
            return null;
        }

        ConcreteSyntaxTree parserOutput = parent.print(env, reducerOutput.left);

        if(parserOutput == null) {
            env.notifyLeftPrintFailed(reducerOutput.right, this);
            return null;
        }

        return parserOutput.consRight(reducerOutput.right);
    }

    @Override
    public String toString() {
        return createString();
    }
}

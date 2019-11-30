package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import at.searles.parsing.printing.PrinterBacktrackException;

/**
 * Parser for chaining parsers. Eg for 5 + 6 where + 6 is the reducer.
 */
public class ParserThenReducer<T, U> implements Parser<U>, Recognizable.Then {

    private final Parser<T> parent;
    private final Reducer<T, U> reducer;

    private final boolean allowParserBacktrack;
    private final boolean allowPrinterBacktrack;

    public ParserThenReducer(Parser<T> parent, Reducer<T, U> reducer, boolean allowParserBacktrack, boolean allowPrinterBacktrack) {
        this.parent = parent;
        this.reducer = reducer;
        this.allowParserBacktrack = allowParserBacktrack;
        this.allowPrinterBacktrack = allowPrinterBacktrack;
    }

    @Override
    public U parse(ParserStream stream) {
        long offset = stream.getOffset();

        // to restore if backtracking
        long preStart = stream.getStart();
        long preEnd = stream.getEnd();

        T t = parent.parse(stream);

        if (t == null) {
            return null;
        }

        // reducer preserves start() in stream and only sets end().
        U u = reducer.parse(stream, t);

        if (u == null) {
            if(offset != stream.getOffset()) {
                throwIfNoBacktrack(stream);
                stream.backtrackToOffset(offset);
            }

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
    public boolean allowParserBacktrack() {
        return allowParserBacktrack;
    }

    @Override
    public ConcreteSyntaxTree print(U u) {
        PartialConcreteSyntaxTree<T> reducerOutput = reducer.print(u);

        if (reducerOutput == null) {
            return null;
        }

        ConcreteSyntaxTree parserOutput = parent.print(reducerOutput.left);

        if (parserOutput == null) {
            if(!allowPrinterBacktrack) {
                throw new PrinterBacktrackException(this, reducerOutput.right);
            }

            return null;
        }

        return parserOutput.consRight(reducerOutput.right);
    }

    @Override
    public String toString() {
        return createString();
    }
}

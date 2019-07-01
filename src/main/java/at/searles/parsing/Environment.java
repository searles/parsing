package at.searles.parsing;

import at.searles.parsing.combinators.ReducerThenReducer;
import at.searles.parsing.printing.PartialStringTree;
import at.searles.parsing.printing.StringTree;

public interface Environment {
    /**
     * This method is triggered when there might be a need to backtrack.
     * If the grammar is supposed to be LL(1) it can be used to throw an exception.
     */
    void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser, Recognizable expected);

    /**
     * In printing, if a string tree is created and at some point the creation is
     * stuck and hence backtracking is required. Optional.
     */
    default void notifyLeftPrintFailed(Recognizable.Then failed, StringTree rightTree) {
        throw new UnsupportedOperationException();
    }
}

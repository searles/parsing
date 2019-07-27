package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;

public interface Environment {
    /**
     * This method is triggered when there might be a need to backtrack.
     * If the grammar is supposed to be LL(1) it can be used to throw an exception.
     *
     * @param stream       The source stream
     * @param failedParser The failed parser. The right parser in this then-parser did not match.
     */
    void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser);

    /**
     * In printing, if a string tree is created and at some point the creation is
     * stuck and hence backtracking is required. Optional.
     */
    default void notifyLeftPrintFailed(ConcreteSyntaxTree rightTree, Recognizable.Then failed) {
        throw new UnsupportedOperationException();
    }
}

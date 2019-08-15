package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;

public interface PrinterCallBack {
    /**
     * In printing, if a string tree is created and at some point the creation is
     * stuck and hence backtracking is required.
     */
    void notifyLeftPrintFailed(ConcreteSyntaxTree rightTree, Recognizable.Then failed);
}

package at.searles.parsing.printing;

import at.searles.parsing.Recognizable;

public class PrinterBacktrackException extends RuntimeException {
    private final Recognizable.Then failedParser;
    private final ConcreteSyntaxTree rightCst;

    public PrinterBacktrackException(Recognizable.Then failedParser, ConcreteSyntaxTree rightCst) {
        this.failedParser = failedParser;
        this.rightCst = rightCst;
    }
}

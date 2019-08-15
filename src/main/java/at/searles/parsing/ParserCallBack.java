package at.searles.parsing;

public interface ParserCallBack {
    /**
     * This method is triggered when there might be a need to backtrack.
     * If the grammar is supposed to be LL(1) it can be used to throw an exception.
     *
     * @param stream       The source stream
     * @param failedParser The failed parser. The right parser in this then-parser did not match.
     */
    void notifyNoMatch(ParserStream stream, Recognizable.Then failedParser);
}

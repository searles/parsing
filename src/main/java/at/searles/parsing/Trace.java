package at.searles.parsing;

/**
 * Use instances of trace to track parsed items though the analysis process.
 */
public interface Trace {
    long getStart();
    long getEnd();
}

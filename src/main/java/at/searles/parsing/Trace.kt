package at.searles.parsing

/**
 * Use instances of trace to track parsed items though the analysis process.
 */
interface Trace {
    val start: Long
    val end: Long
}
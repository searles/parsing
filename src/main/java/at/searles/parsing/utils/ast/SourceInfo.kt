package at.searles.parsing.utils.ast

import java.lang.RuntimeException

/**
 * Instances of this class are assumed to be immutable wrt the methods defined
 * in this interface. Therefore, ParserStream does not implement this interface.
 */
interface SourceInfo {
    /**
     * returns the start position of the underlying stream.
     */
    fun start(): Long

    /**
     * returns the end position of the underlying stream.
     */
    fun end(): Long
}
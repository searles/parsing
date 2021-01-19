package at.searles.parsing.codepoint

import java.lang.RuntimeException

class OutOfBufferRangeException(msg: String): RuntimeException(msg) {
}
package at.searles.parsing.codepoint

import java.lang.RuntimeException

class BufferTooSmallException(msg: String): RuntimeException(msg) {
}
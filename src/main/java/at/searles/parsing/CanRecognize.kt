package at.searles.parsing

interface CanRecognize {
    fun recognize(stream: ParserStream): Boolean
}
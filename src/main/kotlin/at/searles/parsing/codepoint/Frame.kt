package at.searles.parsing.codepoint

interface Frame: CharSequence {
    fun getCodePointStream(): CodePointStream

    fun forEach(fn: (codePoint: Int) -> Unit) {
        val stream = getCodePointStream()
        var codePoint = stream.read()
        while(codePoint != -1) {
            fn(codePoint)
            codePoint = stream.read()
        }
    }

    fun <R> fold(init: R, fn: (init: R, codePoint: Int) -> R): R {
        val stream = getCodePointStream()
        var codePoint = stream.read()
        var value = init

        while(codePoint != -1) {
            value = fn(value, codePoint)
            codePoint = stream.read()
        }

        return value
    }

    override fun toString(): String
}
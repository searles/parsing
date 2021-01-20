package at.searles.parsing.parser

interface RecognizerResult {
    val isSuccess: Boolean
    val index: Long
    val length: Long

    companion object {
        fun of(index: Long, length: Long): RecognizerResult =
            object: RecognizerResult {
                override val isSuccess: Boolean = true
                override val index: Long = index
                override val length: Long = length
            }

        val failure = object: RecognizerResult {
            override val isSuccess: Boolean = false
            override val index: Long get() = error("no index in failure")
            override val length: Long get() = error("no length in failure")
        }
    }
}
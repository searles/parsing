package at.searles.parsing.parser

class PrintInject(private val outputFn: () -> String): Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        return RecognizerResult.success(stream.index, 0)
    }

    override val output: String
        get() = outputFn()
}
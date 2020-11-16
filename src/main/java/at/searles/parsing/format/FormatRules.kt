package at.searles.parsing.format

class FormatRules {
    var indentation = "    "
    var newline = "\n"

    private val rules = HashMap<Any, (context: CodeFormatContext) -> Unit>()

    fun addRule(marker: Any, rule: (context: CodeFormatContext) -> Unit) {
        rules[marker] = rule
    }

    fun invokePreRule(marker: Any, context: CodeFormatContext) {
        rules[marker]?.invoke(context)
    }
}
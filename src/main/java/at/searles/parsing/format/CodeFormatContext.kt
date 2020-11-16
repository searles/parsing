package at.searles.parsing.format

class CodeFormatContext(val rules: FormatRules, val printer: Printer) {
    private var atBeginningOfFile = true
    private var mustInsertEmptyLine = false
    private var mustInsertNewLine = false
    private var mustInsertSpace = false
    private var indentLevel = 0

    fun insertSpace() {
        mustInsertSpace = true
    }

    fun insertNewLine() {
        mustInsertNewLine = true
    }

    fun insertEmptyLine() {
        mustInsertEmptyLine = true
    }

    fun indent() {
        indentLevel++
    }

    fun unindent() {
        indentLevel--
    }

    fun applyFormatting() {
        if(atBeginningOfFile) {
            mustInsertEmptyLine = false
            mustInsertNewLine = false
            mustInsertSpace = false

            atBeginningOfFile = false
        }

        if(mustInsertEmptyLine || mustInsertNewLine) {
            if(mustInsertEmptyLine) {
                printer.print(rules.newline)
            }

            printer.print(rules.newline)

            mustInsertEmptyLine = false
            mustInsertNewLine = false
            mustInsertSpace = false

            if(indentLevel > 0) {
                printer.print(rules.indentation.repeat(indentLevel))
            }
        }

        if(mustInsertSpace) {
            printer.print(" ")
            mustInsertSpace = false
        }
    }

    fun format(marker: Any) {
        rules.invokePreRule(marker, this)
    }

    fun fork(): CodeFormatContext {
        return CodeFormatContext(rules, printer).also {
            it.atBeginningOfFile = atBeginningOfFile
            it.mustInsertEmptyLine = mustInsertEmptyLine
            it.mustInsertNewLine = mustInsertNewLine
            it.mustInsertSpace = mustInsertSpace
            it.indentLevel = indentLevel
        }
    }

    override fun toString(): String {
        return "CodeFormatContext(rules=$rules, printer=$printer, atBeginningOfFile=$atBeginningOfFile, mustInsertEmptyLine=$mustInsertEmptyLine, mustInsertNewLine=$mustInsertNewLine, mustInsertSpace=$mustInsertSpace, indentLevel=$indentLevel)"
    }


}
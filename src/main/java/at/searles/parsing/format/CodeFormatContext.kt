package at.searles.parsing.format

import at.searles.parsing.printing.CodePrinter

class CodeFormatter() {
    private var atBeginningOfFile = true
    private var mustInsertEmptyLine = false
    private var mustInsertNewLine = false
    private var mustInsertSpace = false
    private var indentLevel = 0

    var indentation = "    "
    var newline = "\n"

    private val preRules = HashMap<Any, (printer: Printer) -> Unit>()
    private val postRules = HashMap<Any, (printer: Printer) -> Unit>()

    fun addPreRule(marker: Any, rule: (printer: Printer) -> Unit) {
        preRules[marker] = rule
    }

    fun addPostRule(marker: Any, rule: (printer: Printer) -> Unit) {
        postRules[marker] = rule
    }

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

    fun insertSpaces() {
        if(atBeginningOfFile) {
            mustInsertEmptyLine = false
            mustInsertNewLine = false
            mustInsertSpace = false

            atBeginningOfFile = false
        }

        if(mustInsertEmptyLine) {
            print(newline)
            print(newline)

            mustInsertEmptyLine = false
            mustInsertNewLine = false
            mustInsertSpace = false

            print(indentation.repeat(indentLevel))
        }

        if(mustInsertNewLine) {
            print(newline)

            mustInsertNewLine = false
            mustInsertSpace = false

            print(indentation.repeat(indentLevel))
        }

        if(mustInsertSpace) {
            print(" ")
            mustInsertSpace = false
        }
    }

    fun preFormat(marker: Any, printer: Printer) {
        preRules[marker]?.invoke(printer)
    }

    fun postFormat(marker: Any, printer: Printer) {
        postRules[marker]?.invoke(printer)
    }
}
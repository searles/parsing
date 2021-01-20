package at.searles.parsing.printer

interface PartialPrintTree<out A> {
    val isSuccess get() = true
    val leftValue: A
    val rightTree: PrintTree

    companion object {
        val failure = object: PartialPrintTree<Nothing> {
            override val isSuccess: Boolean = false
            override val leftValue: Nothing get() { error("failure") }
            override val rightTree: PrintTree get() { error("failure") }
        }

        fun <A> of(leftValue: A, rightTree: PrintTree): PartialPrintTree<A> {
            return object: PartialPrintTree<A> {
                override val leftValue: A = leftValue
                override val rightTree: PrintTree = rightTree
            }
        }
    }
}
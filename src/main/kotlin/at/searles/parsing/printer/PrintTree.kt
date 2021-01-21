package at.searles.parsing.printer

interface PrintTree {
    val isSuccess get() = true

    fun print(outStream: OutStream)

    fun asString(): String {
        return  StringOutStream().also {
            this.print(it)
        }.toString()
    }

    operator fun plus(right: PrintTree): PrintTree {
        // flatten and remove empty.
        if(right == Empty) {
            return this
        }

        return ComposedTree.of(this, right)
    }

    object Empty : PrintTree {
        override fun print(outStream: OutStream) {}
        override fun plus(right: PrintTree): PrintTree {
            return right
        }
    }

    companion object {
        fun of(charSequence: CharSequence): PrintTree {
            return StringPrintTree(charSequence.toString())
        }

        val failure = object: PrintTree {
            override val isSuccess: Boolean = false
            override fun print(outStream: OutStream) { error("failure") }
            override fun plus(right: PrintTree): PrintTree { error("failure") }
        }

        val empty = of("")
    }
}
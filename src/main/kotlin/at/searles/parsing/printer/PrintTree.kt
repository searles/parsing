package at.searles.parsing.printer

interface PrintTree {

    fun print(outStream: OutStream)

    operator fun plus(right: PrintTree): PrintTree {
        if(right == Empty) {
            return this
        }

        return ComposedTree(listOf(this, right))
    }

    fun asString(): String {
        val os = StringOutStream()
        print(os)
        return os.toString()
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
    }
}
package at.searles.parsing

/**
 * Everything that can be recognized
 */
interface Recognizable {

    fun recognize(stream: ParserStream): Boolean

    interface Then : Recognizable {

        val left: Recognizable
        val right: Recognizable

        override fun recognize(stream: ParserStream): Boolean {
            val offset = stream.offset
            val preStart = stream.start
            val preEnd = stream.end

            if (!left.recognize(stream)) {
                return false
            }

            val start = stream.start

            if (!right.recognize(stream)) {
                if (stream.offset != offset) {
                    stream.requestBacktrackToOffset(this, offset)
                }

                stream.start = preStart
                stream.end = preEnd

                return false
            }

            stream.start = start
            return true
        }

        fun createString(): String {
            val leftString = when(left) {
                is Or -> "($left)"
                else -> "$left"
            }

            val rightString = when(right) {
                is Or -> "($right)"
                is Then -> "($right)"
                else -> "$left"
            }

            return "$leftString $rightString"
        }
    }

    interface Or : Recognizable {

        val choice0: Recognizable
        val choice1: Recognizable

        override fun recognize(stream: ParserStream): Boolean {
            return choice0.recognize(stream) || choice1.recognize(stream)
        }

        fun createString(): String {
            return if (choice1 is Or) {
                "$choice0 | ($choice1)"
            } else {
                "$choice0 | $choice1"
            }
        }
    }

    interface Opt : Recognizable {

        val parent: Recognizable

        override fun recognize(stream: ParserStream): Boolean {
            stream.start = stream.end
            parent.recognize(stream)
            return true
        }

        fun createString(): String {
            return if (parent is Or || parent is Then) {
                 "($parent)?"
            } else {
                "$parent?"
            }
        }
    }

    interface Rep : Recognizable {

        val parent: Recognizable

        override fun recognize(stream: ParserStream): Boolean {
            val start = stream.start

            while (parent.recognize(stream)) {
                stream.start = start
            }

            return true
        }

        fun createString(): String {
            return if (parent is Or || parent is Then) {
                "($parent)*"
            } else {
                "$parent*"
            }
        }
    }
}
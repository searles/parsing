package at.searles.parsing.parser

interface FnResult<out A> {
    val isSuccess: Boolean
    val value: A

    companion object {
        fun <T> success(value: T): FnResult<T> = object: FnResult<T> {
            override val isSuccess: Boolean = true
            override val value: T = value
        }

        val failure: FnResult<Nothing> = object: FnResult<Nothing> {
            override val isSuccess: Boolean = false
            override val value: Nothing get() { error("No value in failure") }
        }

        fun <T> ofNullable(value: T?): FnResult<T> =
            if(value != null) success(value) else failure
    }
}
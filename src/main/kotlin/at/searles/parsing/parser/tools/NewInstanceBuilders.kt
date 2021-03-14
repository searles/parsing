package at.searles.parsing.parser.tools

import at.searles.parsing.parser.*
import at.searles.parsing.parser.tools.reflection.NewInstanceCreator
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

object NewInstanceBuilders {
    inline fun <reified T> newInstance(vararg ctorArgs: Any?): NewInstance<T> {
        return NewInstance(T::class, ctorArgs.toList())
    }

    class NewInstance<U>(val resultClass: KClass<*>, val ctorArgs: List<Any?>) {
        inline fun <reified T> from(): NewInstanceConversion<T, U> {
            return NewInstanceConversion(resultClass, ctorArgs)
        }

        @ExperimentalStdlibApi
        inline fun <reified T> left(): NewInstanceLeftBuilder<T, U> {
            var type = typeOf<T>()
            var countLeft = 1

            while(type.classifier == Pair::class) {
                type = type.arguments[0].type!!
                countLeft ++
            }

            return NewInstanceLeftBuilder(resultClass, countLeft, ctorArgs)
        }
    }

    class NewInstanceConversion<T, U>(private val outClass: KClass<*>, private val ctorArgs: List<Any?>) : Conversion<T, U> {
        private val newInstanceCreator = NewInstanceCreator<U>(outClass)

        override fun convert(value: T): U {
            val args = createListFromPairs(value)
            return newInstanceCreator.create(ctorArgs + args)
        }

        override fun invert(value: U): FnResult<T> {
            val list = newInstanceCreator.invert(value)

            if(list.take(ctorArgs.size) != ctorArgs) {
                return FnResult.failure
            }

            val args = list.drop(ctorArgs.size)

            @Suppress("UNCHECKED_CAST")
            return FnResult.success(createPairsFromList(args) as T)
        }

        override fun toString(): String {
            return "newInstance(${outClass.simpleName})"
        }
    }

    class NewInstanceLeftBuilder<T, V>(val resultClass: KClass<*>, val countLeft: Int, val ctorArgs: List<Any?>) {
        inline fun <reified U> from(): Fold<T, U, V> {
            return NewInstanceFold(countLeft, resultClass, ctorArgs)
        }

        fun create(): Conversion<T, V> {
            return NewInstanceConversion(resultClass, ctorArgs)
        }
    }

    class NewInstanceFold<T, U, V>(private val countLeft: Int, resultClass: KClass<*>, private val ctorArgs: List<Any?>) : Fold<T, U, V> {
        private val newInstanceCreator = NewInstanceCreator<V>(resultClass)
        override fun fold(left: T, right: U): V {
            val leftArgs = createListFromPairs(left)

            require(leftArgs.size == countLeft)

            val rightArgs = createListFromPairs(right)

            return newInstanceCreator.create(ctorArgs + leftArgs + rightArgs)
        }

        override fun invertLeft(value: V): FnResult<T> {
            val list = newInstanceCreator.invert(value)

            if(list.take(ctorArgs.size) != ctorArgs) {
                return FnResult.failure
            }

            val args = list.drop(ctorArgs.size)

            @Suppress("UNCHECKED_CAST")
            return FnResult.success(createPairsFromList(args.take(countLeft)) as T)
        }

        override fun invertRight(value: V): FnResult<U> {
            val list = newInstanceCreator.invert(value)

            if(list.take(ctorArgs.size) != ctorArgs) {
                return FnResult.failure
            }

            val args = list.drop(ctorArgs.size)

            @Suppress("UNCHECKED_CAST")
            return FnResult.success(createPairsFromList(args.drop(countLeft)) as U)
        }
    }

    inline operator fun <reified T, U> Parser<T>.plus(newInstance: NewInstance<U>): Parser<U> {
        return this + newInstance.from()
    }

    inline operator fun <T, reified U, V> Parser<U>.plus(newInstance: NewInstanceLeftBuilder<T, V>): Reducer<T, V> {
        return this + newInstance.from()
    }

    operator fun <T, V> Recognizer.plus(newInstance: NewInstanceLeftBuilder<T, V>): Reducer<T, V> {
        return this + newInstance.create()
    }

    fun createPairsFromList(list: List<Any?>): Any? {
        return list.reduce { pairs, value -> Pair(pairs, value) }
    }

    fun <T> createListFromPairs(value: T): ArrayList<Any?> {
        val args = ArrayList<Any?>()

        var v: Any? = value

        while (v is Pair<*, *>) {
            args.add(v.second)
            v = v.first
        }

        args.add(v)
        args.reverse()
        return args
    }
}

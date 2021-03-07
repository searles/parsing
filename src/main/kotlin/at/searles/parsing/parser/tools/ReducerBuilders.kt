package at.searles.parsing.parser.tools

import at.searles.parsing.parser.*
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

object ReducerBuilders {
    inline fun <reified T> cast(): Cast<T> {
        return Cast()
    }

    inline fun <reified T> castAll(): CastAll<T> {
        return CastAll()
    }

    inline fun <reified T> newInstance(): NewInstance<T> {
        return NewInstance(T::class)
    }

    class Cast<U> {
        inline fun <reified T: U> from(): Conversion<T, U> {
            return object: Conversion<T, U> {
                override fun convert(value: T): U {
                    return value
                }

                override fun print(value: U): PartialPrintTree<T> {
                    return if(value is T)
                        PartialPrintTree.of(value, PrintTree.Empty)
                    else
                        PartialPrintTree.failure
                }

                override fun toString(): String {
                    return "cast(${T::class.simpleName})"
                }
            }
        }
    }

    class CastAll<U> {
        inline fun <reified T: U> from(): Conversion<List<T>, List<U>> {
            return object: Conversion<List<T>, List<U>> {
                override fun convert(value: List<T>): List<U> {
                    return value
                }

                override fun invert(value: List<U>): FnResult<List<T>> {
                    if(value.any { it !is T }) return FnResult.failure
                    @Suppress("UNCHECKED_CAST")
                    return FnResult.success(value as List<T>)
                }

                override fun toString(): String {
                    return "{castAll<${T::class.java.simpleName}>}"
                }
            }
        }
    }

    class NewInstance<U>(val kClass: KClass<*>) {
        inline fun <reified T> from(): NewInstanceConversion<T, U> {
            return NewInstanceConversion(kClass)
        }
    }

    class NewInstanceConversion<T, U>(private val outClass: KClass<*>) : Conversion<T, U> {
        private val ctor = outClass.constructors.first()
        private val fields = outClass.memberProperties
        private val matchingFields = ctor.parameters.map { parameter ->
            fields.find { it.name == parameter.name } ?: error("no field for ${parameter.name}")
        }

        override fun convert(value: T): U {
            val args = createListFromPairs(value)

            checkArgs(args)

            @Suppress("UNCHECKED_CAST")
            return ctor.call(*args.toTypedArray()) as U
        }

        private fun checkArgs(args: ArrayList<Any?>) {
            val errors = ArrayList<String>()

            ctor.parameters.forEachIndexed { index, kParameter ->
                if(args.size < index) {
                    errors.add("Too few arguments: argument $index is missing. Expected type is ${kParameter.type}")
                } else if(args[index] != null && !kParameter.type.jvmErasure.isInstance(args[index])) {
                    errors.add("argument $index (${args[index]}) does not match ${kParameter.type}")
                }
            }

            if(args.size > ctor.parameters.size) {
                errors.add("Too many arguments: Only ${ctor.parameters.size} expected!")
            }

            if(errors.isNotEmpty()) {
                error(errors.joinToString("\n"))
            }
        }

        override fun invert(value: U): FnResult<T> {
            val list = ArrayList<Any?>()
            for(field in matchingFields) {
                list.add(field.getter.call(value))
            }

            val pairs = createPairsFromList(list)

            @Suppress("UNCHECKED_CAST")
            return FnResult.success(pairs as T)
        }

        private fun createListFromPairs(value: T): ArrayList<Any?> {
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

        private fun createPairsFromList(list: List<Any?>): Any? {
            return list.reduce { pairs, value -> Pair(pairs, value) }
        }

        override fun toString(): String {
            return "newInstance(${outClass.simpleName})"
        }
    }

    inline operator fun <reified T: U, U> Parser<T>.plus(cast: Cast<U>): Parser<U> {
        return this + cast.from()
    }

    inline operator fun <reified T, U> Parser<T>.plus(newInstance: NewInstance<U>): Parser<U> {
        return this + newInstance.from()
    }
}

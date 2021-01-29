package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

object NewInstance {
    inline fun <reified T> of(): CreateFactory<T> {
        return CreateFactory(T::class)
    }

    class CreateFactory<T>(val kClass: KClass<*>) {
        inline fun <reified I> create(): ReflectionMapping<I, T> {
            return ReflectionMapping(kClass)
        }
    }

    class ReflectionMapping<I, T>(outClass: KClass<*>) : Conversion<I, T> {
        private val ctor = outClass.constructors.first()
        private val fields = outClass.memberProperties
        private val matchingFields = ctor.parameters.map { parameter ->
            fields.find { it.name == parameter.name } ?: error("no field for ${parameter.name}")
        }

        override fun convert(value: I): T {
            val args = createListFromPairs(value)

            @Suppress("UNCHECKED_CAST")
            return ctor.call(*args.toTypedArray()) as T
        }

        override fun invert(value: T): FnResult<I> {
            val list = ArrayList<Any?>()
            for(field in matchingFields) {
                list.add(field.getter.call(value))
            }

            val pairs = createPairsFromList(list)

            @Suppress("UNCHECKED_CAST")
            return FnResult.success(pairs as I)
        }


        private fun createListFromPairs(value: I): ArrayList<Any?> {
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
            return list.reduce { pairs, value,  -> Pair(pairs, value) }
        }
    }
}

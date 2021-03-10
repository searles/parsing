package at.searles.parsing.parser.tools.reflection

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

class NewInstanceCreator<T>(outClass: KClass<*>) {
    private val ctor = outClass.constructors.first()
    private val ctorParameterToField = ctor.parameters.map { parameter ->
        outClass.memberProperties.find { it.name == parameter.name } ?: error("no field for ${parameter.name}")
    }

    private fun checkArgs(args: List<Any?>) {
        val errors = ArrayList<String>()

        ctor.parameters.forEachIndexed { index, kParameter ->
            if(args.size <= index) {
                errors.add("arg[$index] is missing. Expected type is ${kParameter.type}")
            } else if(args[index] != null && !kParameter.type.jvmErasure.isInstance(args[index])) {
                errors.add("arg[$index] must be of type ${kParameter.type} but is ${args[index]}")
            }
        }

        if(args.size > ctor.parameters.size) {
            errors.add("Too many arguments: Only ${ctor.parameters.size} expected!")
        }

        if(errors.isNotEmpty()) {
            error(errors.joinToString("\n"))
        }
    }

    fun create(args: List<Any?>): T {
        checkArgs(args)

        @Suppress("UNCHECKED_CAST")
        return ctor.call(*args.toTypedArray()) as T
    }

    fun invert(value: T): ArrayList<Any?> {
        val list = ArrayList<Any?>()

        for(field in ctorParameterToField) {
            list.add(field.getter.call(value))
        }

        return list
    }
}
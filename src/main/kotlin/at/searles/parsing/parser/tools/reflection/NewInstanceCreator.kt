package at.searles.parsing.parser.tools.reflection

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

class NewInstanceCreator<T>(private val outClass: KClass<*>) {
    init {
        require(outClass.constructors.size == 1) { "${outClass.simpleName} must have exactly one constructor" }
    }

    private val ctor = outClass.constructors.first()
    private val ctorParameterToField = ctor.parameters.map { parameter ->
        outClass.memberProperties.find { it.name == parameter.name } ?: error("no field for ${parameter.name}")
    }

    private fun checkArgs(args: List<Any?>) {
        if(args.size != ctor.parameters.size) {
            generateError(args)
        }

        ctor.parameters.forEachIndexed { index, kParameter ->
            if(args[index] != null && !kParameter.type.jvmErasure.isInstance(args[index])) {
                generateError(args)
            }
        }
    }

    private fun generateError(args: List<Any?>) {
        val expected = ctor.parameters.joinToString(", ") { it.javaClass.simpleName }
        val actual = args.joinToString(", ") { it?.javaClass?.simpleName ?: "Any?" }

        error("${outClass.simpleName}($expected) does not match arguments ($actual)")
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
package at.searles.parsing.utils.builder

import at.searles.parsing.ParserStream
import at.searles.parsing.utils.ast.SourceInfo
import java.lang.IllegalArgumentException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

class GenFactory<T>(private val clazz: Class<*>, private val parameters: Array<String>) {
    private val getters: Array<Member>

    private var ctor: Constructor<*>? = null
    private var ctorWithoutInfo: Constructor<*>? = null
    private var defaultCtor: Constructor<*>? = null
    private var setters: Array<Method>? = null

    init {
        getters = Array(parameters.size) { i -> member(parameters[i]) }

        val types = Array(parameters.size) { i -> type(getters[i]) }

        try {
            // try to get a proper constructor. First with source info
            ctor = clazz.getConstructor(SourceInfo::class.java, *types)
        } catch (e: NoSuchMethodException) {
            // now without
            try {
                ctorWithoutInfo = clazz.getConstructor(*types)
            } catch (e: NoSuchMethodException) {
                defaultCtor = clazz.getConstructor()
                setters = Array(parameters.size) { i -> clazz.getMethod(methodAccess("set", parameters[i]), types[i]) }
            }
        }
    }

    private fun member(parameter: String): Member {
        return try {
            clazz.getMethod(methodAccess("get", parameter)) // public getter
        } catch (e: NoSuchMethodException) {
            val field = clazz.getDeclaredField(parameter) // private field
            field.isAccessible = true
            field
        }
    }

    private fun type(m: Member): Class<*> {
        return when (m) {
            is Field -> m.type
            is Method -> m.returnType
            else -> throw IllegalArgumentException("Field or method expected: $m")
        }
    }

    private fun methodAccess(prefix: String, property: String): String {
        return prefix + property.substring(0, 1).toUpperCase() + property.substring(1)
    }

    private fun fetch(obj: Any, m: Member): Any? {
        return when (m) {
            is Field -> m.get(obj)
            is Method -> m.invoke(obj)
            else -> throw IllegalArgumentException("Field or method expected: $m")
        }
    }

    fun toProperties(obj: T): Properties? { // this could also simply return a map...
        if(!clazz.isInstance(obj)) {
            return null
        }

        val map = HashMap<String, Any>().also { m ->
            parameters.forEachIndexed { i, parameter ->
                fetch(obj!!, getters[i])?.let { m.put(parameter, it) }
            }
        }

        return Properties(map)
    }

    fun fromProperties(stream: ParserStream, properties: Properties): T {
        if (defaultCtor != null) {
            val pojo = defaultCtor!!.newInstance()
            setters!!.forEachIndexed { index, setter ->
                properties.get<T>(parameters[index])?.let { setter.invoke(pojo, properties[parameters[index]]) }
            }

            @Suppress("UNCHECKED_CAST")
            return pojo as T
        }

        val arguments: Array<Any?> = Array(parameters.size) { i -> properties.get<T>(parameters[i]) }

        return if (ctor != null) {
            @Suppress("UNCHECKED_CAST")
            ctor!!.newInstance(stream.createSourceInfo(), *arguments) as T
        } else {
            @Suppress("UNCHECKED_CAST")
            ctorWithoutInfo!!.newInstance(*arguments) as T
        }
    }
}
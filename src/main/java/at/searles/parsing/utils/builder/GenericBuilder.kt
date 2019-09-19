import at.searles.parsing.ParserStream
import at.searles.parsing.utils.ast.SourceInfo
import java.lang.IllegalArgumentException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

internal class GenericBuilder<T>(private val clazz: Class<T>, private val parameters: Array<String>) {
    private lateinit var map: Map<String, Any>

    private val getters: Array<Member>

    private var ctor: Constructor<T>? = null
    private var ctorWithoutInfo: Constructor<T>? = null
    private var defaultCtor: Constructor<T>? = null
    private var setters: Array<Method>? = null

    init {
        getters = Array(parameters.size) { i -> member(parameters[i]) }

        val types = Array(parameters.size) { i -> type(getters[i]) }

        try {
            // try to get a proper constructor. First with source info
            ctor = clazz.getConstructor(SourceInfo::class.java, *types)
        } catch (e: NoSuchMethodException) {
            // now without
            ctorWithoutInfo = clazz.getConstructor(*types)
        } catch (e: NoSuchMethodException) {
            defaultCtor = clazz.getConstructor()
            setters = Array(parameters.size) { i -> clazz.getMethod(methodAccess("set", parameters[i]), types[i]) }
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

    /**
     * Initialize an empty generic builder for the given class.
     *
     * emptyMap().then(
     *  setter<T>("name", name())
     * ).then(
     *  GenericBuilder<>(Animal.class, "condition", "thenPart", "elsePart")
     * )
     */
    fun init(): GenericBuilder<T> {
        map = HashMap()
        return this
    }

    fun isEmpty(): Boolean = map.isEmpty()


    fun init(obj: T): GenericBuilder<T> { // this could also simply return a map...
        map = HashMap<String, Any>().also { m ->
            parameters.forEachIndexed { i, parameter ->
                fetch(obj!!, getters[i])?.let { m.put(parameter, it) }
            }
        }

        return this
    }


    operator fun set(id: String, value: Any?): GenericBuilder<T> {
        if (value == null) {
            return this
        }

        val copy = GenericBuilder(clazz, parameters)
        copy.map = HashMap(map).also { it[id] = value }
        return copy
    }

    fun unset(id: String): GenericBuilder<T>? {
        if (!map.containsKey(id)) {
            return null;
        }

        val copy = GenericBuilder(clazz, parameters)
        copy.map = HashMap(map).also { it.remove(id) }

        return copy
    }

    operator fun get(id: String): Any? {
        return map[id]
    }

    fun build(stream: ParserStream): T {
        if (defaultCtor != null) {
            val pojo = defaultCtor!!.newInstance()
            setters!!.forEachIndexed { index, setter ->
                map[parameters[index]]?.let { setter.invoke(pojo, map[parameters[index]]) }
            }

            return pojo
        }

        val arguments: Array<Any?> = Array(parameters.size) { i -> map[parameters[i]] }

        return if (ctor != null) {
            ctor!!.newInstance(stream.createSourceInfo(), *arguments)
        } else {
            ctorWithoutInfo!!.newInstance(*arguments)
        }
    }
}
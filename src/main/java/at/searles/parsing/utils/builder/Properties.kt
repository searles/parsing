package at.searles.parsing.utils.builder

class Properties : Iterable<String> {
    private val map: HashMap<String, Any>

    constructor() {
        this.map = HashMap()
    }

    constructor(map: java.util.HashMap<String, Any>) {
        this.map = map
    }

    override fun iterator(): Iterator<String> {
        return map.keys.iterator();
    }

    fun isEmpty(): Boolean = map.isEmpty()

    fun concat(id: String, value: Any?): Properties {
        if (value == null) {
            return this
        }

        return Properties().also {
            it.map.putAll(map)
            it.map[id] = value
        }
    }

    fun diff(id: String): Properties? {
        if (!map.containsKey(id)) {
            return null
        }

        return Properties().also {
            it.map.putAll(map)
            it.map.remove(id)
        }
    }

    operator fun <T> get(id: String): T? {
        @Suppress("UNCHECKED_CAST")
        return map[id] as T?
    }

    fun size(): Int {
        return map.size
    }
}
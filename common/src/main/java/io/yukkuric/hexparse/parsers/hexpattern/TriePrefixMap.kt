package io.yukkuric.hexparse.parsers.hexpattern

class TriePrefixMap {
    val next = HashMap<Char, TriePrefixMap>()
    var value: String? = null

    operator fun set(prefix: String, data: String) {
        var ptr = this
        for (chr in prefix) {
            ptr = ptr.next.getOrPut(chr) { TriePrefixMap() }
        }
        ptr.value = data
    }

    operator fun get(fullStr: String): String? {
        var ptr = this
        var len = 0
        for (chr in fullStr) {
            len++
            ptr = ptr.next[chr] ?: return null
            ptr.value?.let { return "$it${fullStr.substring(len).trim()}" }
        }
        return null
    }

    fun clear() = next.clear()
}
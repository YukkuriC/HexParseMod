package io.yukkuric.hexparse.parsers.hexpattern

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.locale.Language

object DotHexPatternMapper {
    val nameMap = HashMap<String, String>()
    var serverNameMap: Map<String, String>? = null // TODO collect en_us lang from server
    val KeepSelfKeys = setOf("(", ")", "[", "]", "{", "}")
    val RegLineSep = Regex("\\s*,\\s*")
    val RegListUnwrap = Regex("(\\[+)(.*?)(]+)")

    @JvmStatic
    fun doCollect() {
        if (nameMap.isNotEmpty()) return
        val hexAPI = HexAPI.instance()
        for (entry in HexActions.REGISTRY.entrySet()) {
            val langKey = hexAPI.getActionI18nKey(entry.key)
            val display = Language.getInstance().getOrDefault(langKey)
            if (display != langKey) nameMap[display] = entry.key.location().toString()
        }
    }

    @JvmStatic
    operator fun get(display: String) = nameMap[display] ?: serverNameMap?.let { it[display] }

    @JvmStatic
    fun processCode(code: String): String {
        // try init client-side map here
        doCollect()

        // we just remap per-line for now
        val pieces = code.split("\n").map(String::trim)

        val collected = StringBuilder()
        for (p in pieces) {
            collected.append(" ${processOne(p)}")
        }

        return collected.toString()
    }

    fun processOne(p: String): String {
        if (p.startsWith("<") && p.endsWith(">")) {
            val unwrapped = p.substring(1, p.length - 1)
            // brute breakdown for lists
            if (unwrapped.startsWith('[') || unwrapped.endsWith(']')) {
                val cut = unwrapped.split(RegLineSep)
                if (cut.size > 1) return processCode(cut.joinToString("\n"))
                var p1 = ""
                var p2 = ""
                val center = unwrapped.replace(RegListUnwrap) { match ->
                    val (m1, mc, m2) = match.destructured
                    p1 = m1
                    p2 = m2
                    mc
                }
                val rebuild = ArrayList<String>()
                if (p1.isNotEmpty()) rebuild.add(p1)
                rebuild.add(processOne(center))
                if (p2.isNotEmpty()) rebuild.add(p2)
                return rebuild.joinToString(" ")
            }

            // TODO other consts
            return unwrapped
        }
        if (p in KeepSelfKeys) return p

        // TODO special handlers
        // TODO raw patterns
        val actionKey = this[p] ?: return p.replace(" ", "")
        return actionKey
    }
}
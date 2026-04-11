package io.yukkuric.hexparse.parsers.hexpattern

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.locale.Language

object DotHexPatternMapper {
    val nameMap = HashMap<String, String>()
    var serverNameMap: Map<String, String>? = null // TODO collect en_us lang from server

    @JvmStatic
    fun doCollect() {
        if (nameMap.isNotEmpty()) return
        val hexAPI = HexAPI.instance()
        for (entry in HexActions.REGISTRY.entrySet()) {
            val display = Language.getInstance().getOrDefault(hexAPI.getActionI18nKey(entry.key))
            nameMap[display] = entry.key.location().toString()
        }
    }

    @JvmStatic
    operator fun get(display: String) = nameMap[display] ?: serverNameMap?.let { it[display] }

    @JvmStatic
    fun processCode(code: String): String {
        // we just remap per-line for now
        val pieces = code.split("\n").map(String::trim)

        val collected = StringBuilder()
        for (p in pieces) {
            if (p.startsWith("<") && p.endsWith(">")) {
                // TODO
                collected.append(p.substring(1, p.length - 1))
                continue
            }
            // TODO special handlers
            // TODO raw patterns
            val actionKey = this[p]
            if (actionKey == null) {
                collected.append(" comment_UNKNOWN_${p.replace(" ", "")}")
                continue
            }
            collected.append(" $actionKey")
        }

        return collected.toString()
    }
}
package io.yukkuric.hexparse.parsers.hexpattern

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.hex.HexActions
import io.yukkuric.hexparse.network.MsgHandlers
import io.yukkuric.hexparse.network.MsgSyncDisplayMap
import net.minecraft.locale.Language
import net.minecraft.server.level.ServerPlayer

// this.mapper=Java.loadClass('io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper').INSTANCE
// mapper.nameMap.clear()
// mapper.doCollect()
object DotHexPatternMapper {
    val nameMap = HashMap<String, String>()
    val prefixMap = HashMap<String, String>()
    val prefixMapTrie = TriePrefixMap()
    var serverNameMap: Map<String, String>? = null
    val serverPrefixMap = TriePrefixMap()
    @JvmStatic
    fun receiveRemoteMap(packet: MsgSyncDisplayMap) {
        clearServer()
        serverNameMap = packet.map
        for (entry in packet.prefixMap) {
            serverPrefixMap[entry.key] = entry.value
        }
    }
    @JvmStatic
    fun sendRemoteMap(player: ServerPlayer) {
        MsgHandlers.SERVER.sendPacketToPlayer(player, MsgSyncDisplayMap(nameMap, prefixMap))
    }
    @JvmStatic
    fun clear() {
        nameMap.clear()
        prefixMap.clear()
        prefixMapTrie.clear()
    }
    @JvmStatic
    fun clearServer() {
        serverNameMap = null
        serverPrefixMap.clear()
    }

    val KeepSelfKeys = setOf("(", ")", "[", "]", "{", "}")
    val RawPatternMap = mapOf(
        Pair("open_paren", "{"),
        Pair("close_paren", "}"),
        Pair("escape", "\\"),
        Pair("undo", "\\"),
    )
    val RawSpecialHandlerMap = hashMapOf(
        Pair("hexcasting:number", "num_"),
        Pair("hexcasting:mask", "mask_"),
        Pair("hexflow:copy_mask", "copy_mask_"),
        Pair("hexflow:noob_num", "num_"),
    )

    val RegLineSep = Regex("((?<=[\\[\\]])?\\s*,\\s*(?=[\\[\\]])?)|(?<=[\\[\\]])|(?=[\\[\\]])")

    @JvmStatic
    fun doCollect() {
        if (nameMap.isNotEmpty()) return
        val hexAPI = HexAPI.instance()
        for (entry in HexActions.REGISTRY.entrySet()) {
            val langKey = hexAPI.getActionI18nKey(entry.key)
            val display = Language.getInstance().getOrDefault(langKey)
            if (display != langKey) nameMap[display] = entry.key.location().toString()
        }
        for (entry in RawPatternMap) {
            val langKey = hexAPI.getRawHookI18nKey(HexAPI.modLoc(entry.key))
            val display = Language.getInstance().getOrDefault(langKey)
            if (display != langKey) nameMap[display] = entry.value
        }

        // special handler prefix
        for (entry in RawSpecialHandlerMap) {
            val langKey = "hexcasting.special.${entry.key}"
            val display = Language.getInstance().getOrDefault(langKey).replace("%s", "").trim()
            if (display != langKey) {
                entry.value.let {
                    prefixMap[display] = it
                    prefixMapTrie[display] = it
                }
            }
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
                return processCode(cut.joinToString("\n"))
            }

            // TODO other consts
            return unwrapped
        }
        if (p in KeepSelfKeys) return p

        // special handlers
        prefixMapTrie[p]?.let { return it }
        serverPrefixMap[p]?.let { return it }

        // TODO raw patterns
        val actionKey = this[p] ?: return p.replace(" ", "")
        return actionKey
    }
}
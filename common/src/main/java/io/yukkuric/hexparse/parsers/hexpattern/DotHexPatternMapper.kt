package io.yukkuric.hexparse.parsers.hexpattern

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.common.lib.hex.HexActions
import io.yukkuric.hexparse.config.HexParseConfig
import io.yukkuric.hexparse.network.MsgHandlers
import io.yukkuric.hexparse.network.MsgSyncDisplayMap
import io.yukkuric.hexparse.parsers.CodeCutter
import net.minecraft.locale.Language
import net.minecraft.server.level.ServerPlayer

// this.mapper=Java.loadClass('io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper').INSTANCE
// mapper.nameMap.clear()
// mapper.doCollect()

// srsly, it's just meaningless
// there's no way to write a formal parser for a format designed only for display
// anyone interested in this hell feel free to PR, I quit
object DotHexPatternMapper {
    val nameMap = HashMap<String, String>()
    val prefixMap = HashMap<String, String>()
    val prefixMapTrie = TriePrefixMap()
    var serverNameMap = HashMap<String, String>()
    val serverPrefixMap = TriePrefixMap()
    @JvmStatic
    fun receiveRemoteMap(packet: MsgSyncDisplayMap) {
        serverNameMap.putAll(packet.map)
        for (entry in packet.prefixMap) {
            serverPrefixMap[entry.key] = entry.value
        }
    }
    @JvmStatic
    fun sendRemoteMap(player: ServerPlayer) {
        if (!HexParseConfig.syncDisplayToClient()) return
        doCollect()
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
        serverNameMap.clear()
        serverPrefixMap.clear()
    }

    val KeepSelfKeys = setOf("(", ")", "[", "]", "{", "}")
    val RawPatternMap = mapOf(
        Pair("open_paren", "{"),
        Pair("close_paren", "}"),
        Pair("escape", "\\"),
        Pair("undo", "undo"),
    )
    val RawSpecialHandlerMap = hashMapOf(
        Pair("hexcasting:number", "num_"),
        Pair("hexcasting:mask", "mask_"),
        Pair("hexflow:copy_mask", "copy_mask_"),
        Pair("hexflow:noob_num", "num_"),
    )
    val EmbeddedRegexSeq: List<Pair<Regex, (MatchResult) -> String>> = listOf(
        Pair(Regex("\\(\\s*([0-9.-e]+)\\s*,\\s*([0-9.-e]+)\\s*,\\s*([0-9.-e]+)\\s*\\)")) {
            val (x, y, z) = it.destructured
            "vec_${x}_${y}_${z}"
        },
        Pair(Regex("(((NORTH|SOUTH)_)?(WEST|EAST))?\\s+(?<sig>[wedsaq]+)")) { "_${it.groups["sig"]?.value}" },
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
    operator fun get(display: String) = nameMap[display] ?: serverNameMap[display]

    @JvmStatic
    fun processCode(code: String, firstCall: Boolean = false): String {
        // try init client-side map here
        doCollect()
        val code = if (firstCall) {
            code.let { CodeCutter.pCommentBlock.matcher(it).replaceAll("") }
                .let { CodeCutter.pCommentLine.matcher(it).replaceAll("") }
        } else code

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

            // other consts
            for (pair in EmbeddedRegexSeq) {
                pair.first.matchAt(unwrapped, 0)?.let { return pair.second(it) }
            }

            return unwrapped
        }
        if (p in KeepSelfKeys) return p

        // special handlers
        prefixMapTrie[p]?.let { return it }
        serverPrefixMap[p]?.let { return it }

        // named patterns
        val actionKey = this[p] ?: return p.replace(" ", "")
        return actionKey
    }
}
package io.yukkuric.hexparse.parsers.nbt2str.plugins

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.PluginIotaFactory
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag

class StringLitParser : INbt2Str {
    override fun match(node: CompoundTag): Boolean = isType(node, PluginIotaFactory.TYPE_STRING)

    override fun parse(node: CompoundTag): String? {
        val data = node.getString(HexIotaTypes.KEY_DATA)
        data.replace("\n", "\\\n")
        data.replace("\"", "\\\"")
        data.replace("\\", "\\\\")
        return '"' + data + '"'
    }
}
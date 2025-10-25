package io.yukkuric.hexparse.parsers.nbt2str.plugins

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.PluginIotaFactory
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag

object IdentifierParser : INbt2Str {
    override fun match(node: CompoundTag) = isType(node, PluginIotaFactory.TYPE_RESLOC)

    override fun parse(node: CompoundTag): String {
        val inner = node.getCompound(HexIotaTypes.KEY_DATA)
        val namespace = inner.getString("namespace")
        val path = inner.getString("path")
        return "id_" +
                if (namespace == "minecraft") path
                else "${namespace}:${path}"

    }
}
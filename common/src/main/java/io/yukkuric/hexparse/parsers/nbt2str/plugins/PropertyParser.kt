package io.yukkuric.hexparse.parsers.nbt2str.plugins

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.PluginIotaFactory
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag

object PropertyParser : INbt2Str {
    fun unwrapName(raw: String) = if (raw.startsWith("_")) raw.substring(1) else raw

    override fun match(node: CompoundTag?): Boolean = isType(node, PluginIotaFactory.TYPE_PROP)

    override fun parse(node: CompoundTag?): String {
        val pack = node!!.getCompound(HexIotaTypes.KEY_DATA)
        val name = pack.getString("name")
        return "prop_${unwrapName(name)}";
    }
}
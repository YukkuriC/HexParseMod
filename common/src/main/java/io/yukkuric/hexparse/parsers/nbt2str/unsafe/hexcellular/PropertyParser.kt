package io.yukkuric.hexparse.parsers.nbt2str.unsafe.hexcellular

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.IPlayerBinder
import io.yukkuric.hexparse.parsers.PluginIotaFactory
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object PropertyParser : INbt2Str, IPlayerBinder {
    fun unwrapName(raw: String) = if (raw.startsWith("_")) raw.substring(1) else raw

    override fun match(node: CompoundTag?): Boolean = isType(node, PluginIotaFactory.TYPE_PROP)

    override fun parse(node: CompoundTag?): String {
        val pack = node!!.getCompound(HexIotaTypes.KEY_DATA)
        val name = pack.getString("name")
        if (name.contains("@")) {
            val sub = name.substring(name.indexOf('@') + 1)
            return "myprop_${sub}"
        }
        return "prop_${unwrapName(name)}"
    }

    private var level: ServerLevel? = null
    override fun collectInnerData(): CompoundTag? {
        if (level == null) return null
        // TODO
        return super.collectInnerData()
    }

    override fun BindPlayer(p: ServerPlayer?) {
        level = p?.serverLevel()
    }
}
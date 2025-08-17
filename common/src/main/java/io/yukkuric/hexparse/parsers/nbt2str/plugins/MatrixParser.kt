package io.yukkuric.hexparse.parsers.nbt2str.plugins

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.parsers.PluginIotaFactory
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag

object MatrixParser : INbt2Str {
    override fun match(node: CompoundTag?) = isType(node, PluginIotaFactory.TYPE_MATRIX)
    override fun parse(node: CompoundTag): String {
        val output = ArrayList<String>()
        output.add("mat")

        val inner = node.getCompound(HexIotaTypes.KEY_DATA)
        output.add(inner.getInt("rows").toString())
        output.add(inner.getInt("cols").toString())
        for (row in inner.getList("mat", CompoundTag.TAG_LIST.toInt()))
            for (num in row as ListTag)
                output.add(displayMinimal((num as DoubleTag).asDouble))

        return java.lang.String.join("_", output)
    }
}
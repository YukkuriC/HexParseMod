package io.yukkuric.hexparse.api

import at.petrak.hexcasting.api.spell.Action
import io.yukkuric.hexparse.misc.IOMethod
import io.yukkuric.hexparse.parsers.ParserMain
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.nbt2str.PatternParser
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object HexParseAPI {
    @JvmStatic
    fun AddForthParser(p: IStr2Nbt) {
        ParserMain.AddForthParser(p)
    }
    @JvmStatic
    fun AddBackParser(p: INbt2Str) {
        ParserMain.AddBackParser(p)
    }
    @JvmStatic
    fun AddSpecialHandlerBackParser(id: String, func: (Action, CompoundTag, ServerPlayer) -> String) {
        PatternParser.AddSpecialHandlerBackParser(id, func)
    }
    @JvmStatic
    fun CreateItemIOMethod(
        cls: Class<*>,
        writer: ((ItemStack, CompoundTag) -> Unit)? = null,
        reader: ((ItemStack) -> CompoundTag?)? = null,
        priority: Int = 0,
        validator: ((ItemStack, Boolean) -> Boolean)? = null,
    ) {
        IOMethod(cls, writer, reader, priority, validator)
    }
}
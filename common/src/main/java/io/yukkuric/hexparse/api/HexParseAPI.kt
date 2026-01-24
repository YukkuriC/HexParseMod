package io.yukkuric.hexparse.api

import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import io.yukkuric.hexparse.misc.IOMethod
import io.yukkuric.hexparse.parsers.ParserMain
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.nbt2str.PatternParser
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt
import net.minecraft.nbt.CompoundTag
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
    fun <T : SpecialHandler> AddSpecialHandlerBackParser(cls: Class<T>, func: (T, CompoundTag) -> String) {
        PatternParser.AddSpecialHandlerBackParser(cls, func)
    }
    @JvmStatic
    fun CreateItemIOMethod(
        cls: Class<*>,
        writer: ((ItemStack, CompoundTag) -> Unit)?,
        reader: ((ItemStack) -> CompoundTag?)?,
        priority: Int = 0
    ) {
        IOMethod(cls, writer, reader, priority)
    }
}
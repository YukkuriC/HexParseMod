@file:Suppress("unused")

package io.yukkuric.hexparse.api

import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import io.yukkuric.hexparse.misc.IOMethod
import io.yukkuric.hexparse.parsers.ParserMain
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.nbt2str.PatternParser
import io.yukkuric.hexparse.parsers.str2nbt.IStr2Nbt
import net.minecraft.world.item.ItemStack

@Suppress("unused", "FunctionName")
object HexParseAPI {
    private val idMapForth = HashMap<String, IStr2Nbt>()
    private val idMapBack = HashMap<String, INbt2Str<*>>()

    @JvmStatic
    fun AddForthParser(p: IStr2Nbt, id: String) {
        idMapForth[id]?.let(::RemoveForthParser)
        idMapForth[id] = p
        ParserMain.AddForthParser(p)
    }
    @JvmStatic
    fun AddBackParser(p: INbt2Str<*>, id: String) {
        idMapBack[id]?.let(::RemoveBackParser)
        idMapBack[id] = p
        ParserMain.AddBackParser(p)
    }
    @JvmStatic
    fun AddForthParser(p: IStr2Nbt) {
        ParserMain.AddForthParser(p)
    }
    @JvmStatic
    fun AddBackParser(p: INbt2Str<*>) {
        ParserMain.AddBackParser(p)
    }
    @JvmStatic
    fun RemoveForthParser(p: IStr2Nbt) {
        ParserMain.RemoveForthParser(p)
    }
    @JvmStatic
    fun RemoveBackParser(p: INbt2Str<*>) {
        ParserMain.RemoveBackParser(p)
    }

    @JvmStatic
    fun <T : SpecialHandler> AddSpecialHandlerBackParser(cls: Class<T>, func: (T, PatternIota) -> String) {
        PatternParser.AddSpecialHandlerBackParser(cls, func)
    }
    @JvmStatic
    fun CreateItemIOMethod(
        cls: Class<*>,
        writer: ((ItemStack, Iota) -> Unit)? = null,
        reader: ((ItemStack) -> Iota?)? = null,
        priority: Int = 0,
        validator: ((ItemStack, Boolean) -> Boolean)? = null,
    ) {
        IOMethod(cls, writer, reader, priority, validator)
    }
}
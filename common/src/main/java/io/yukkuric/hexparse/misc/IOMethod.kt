package io.yukkuric.hexparse.misc

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.getOrCreateCompound
import at.petrak.hexcasting.common.items.storage.ItemFocus
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import at.petrak.hexcasting.common.items.storage.ItemThoughtKnot
import io.yukkuric.hexparse.HexParse
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack


class IOMethod(
    cls: Class<*>, private val writer: ((ItemStack, CompoundTag) -> Unit)?,
    private val reader: ((ItemStack) -> CompoundTag?)?, val priority: Int = 0
) {
    private lateinit var current: ItemStack

    fun write(nbt: CompoundTag) {
        if (writer == null) {
            // simple writer for IotaHolderItem
            current.getOrCreateTag().put("data", nbt)
            return
        }
        writer!!(current, nbt)
    }

    fun read(): CompoundTag? {
        if (reader == null) return (current.item as IotaHolderItem).readIotaTag(current)
        return reader!!(current)
    }

    fun bind(stack: ItemStack) {
        current = stack
    }

    fun rename(newName: String) {
        current.setHoverName(Component.literal(newName))
    }

    fun readIota(world: ServerLevel?): Iota? {
        val tag = read() ?: return null
        return IotaType.deserialize(tag, world)
    }

    init {
        val old = ITEM_IO_TYPES.put(cls, this)
        if (old == null) HexParse.LOGGER.info("IOMethod registered for class {} {}", cls.javaClass, cls)
        else HexParse.LOGGER.error("IOMethod registered for class {} but duplicated", cls)
    }

    companion object {
        private val ITEM_IO_TYPES: MutableMap<Class<*>, IOMethod> = HashMap()

        @JvmStatic
        fun get(stack: ItemStack?): IOMethod? {
            if (stack == null) return null
            val ret = ITEM_IO_TYPES[stack.item.javaClass]
            ret?.bind(stack)
            return ret
        }

        init {
            IOMethod(ItemFocus::class.java, null, null)
            IOMethod(ItemThoughtKnot::class.java, null, null)
            IOMethod(
                ItemSpellbook::class.java,
                { stack: ItemStack?, nbt: CompoundTag? ->
                    val idx = ItemSpellbook.getPage(stack, 1)
                    val pageKey = idx.toString()
                    stack!!.getOrCreateCompound(ItemSpellbook.TAG_PAGES).put(pageKey, nbt)
                }, null
            )
        }
    }
}
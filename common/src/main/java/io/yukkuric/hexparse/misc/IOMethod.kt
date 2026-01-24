package io.yukkuric.hexparse.misc

import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.getOrCreateCompound
import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.common.items.ItemSpellbook
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.yukkuric.hexparse.HexParse
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack


class IOMethod(
    cls: Class<*>, private val writer: ((ItemStack, CompoundTag) -> Unit)? = null,
    private val reader: ((ItemStack) -> CompoundTag?)? = null, val priority: Int = 0,
    private val validator: ((ItemStack, Boolean) -> Boolean)? = null
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
        return HexIotaTypes.deserialize(tag, world)
    }

    init {
        val old = ITEM_IO_TYPES.put(cls, this)
        if (old == null) HexParse.LOGGER.info("IOMethod registered for class {} {}", cls.javaClass, cls)
        else HexParse.LOGGER.error("IOMethod registered for class {} but duplicated", cls)
    }

    companion object {
        private val ITEM_IO_TYPES: MutableMap<Class<*>, IOMethod> = HashMap()

        @JvmStatic
        fun get(stack: ItemStack?, isWrite: Boolean): IOMethod? {
            if (stack == null) return null
            val ret = ITEM_IO_TYPES[stack.item.javaClass]
            if (ret?.validator != null && !ret.validator!!(stack, isWrite)) return null
            ret?.bind(stack)
            return ret
        }

        init {
            IOMethod(ItemFocus::class.java)
            // IOMethod(ItemThoughtKnot::class.java)
            IOMethod(
                ItemSpellbook::class.java,
                writer = { stack: ItemStack?, nbt: CompoundTag? ->
                    val idx = ItemSpellbook.getPage(stack, 1)
                    val pageKey = idx.toString()
                    stack!!.getOrCreateCompound(ItemSpellbook.TAG_PAGES).put(pageKey, nbt)
                }
            )
        }
    }
}
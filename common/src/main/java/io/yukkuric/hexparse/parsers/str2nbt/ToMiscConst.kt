package io.yukkuric.hexparse.parsers.str2nbt

import at.petrak.hexcasting.api.casting.iota.*
import io.yukkuric.hexparse.parsers.IPlayerBinder
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object ToMiscConst : IStr2Nbt, IPlayerBinder {
    val NULL = CompoundTag();

    val ConstMapper = mapOf(
        "self" to { EntityIota(owner) },
        "myself" to { EntityIota(owner) },
        "false" to { BooleanIota(false) },
        "true" to { BooleanIota(true) },
        "null" to { NullIota.INSTANCE },
        "garbage" to { GarbageIota.INSTANCE },
    )

    lateinit var cachedKey: String
    lateinit var owner: Player

    override fun BindPlayer(p: ServerPlayer) {
        owner = p
    }

    override fun match(node: String?): Boolean {
        if (node == null) return false
        cachedKey = node.lowercase()
        return ConstMapper.containsKey(cachedKey)
    }

    override fun parse(node: String?): Iota {
        val getter = ConstMapper[cachedKey] ?: throw RuntimeException("why?")
        return getter.invoke()
    }
}
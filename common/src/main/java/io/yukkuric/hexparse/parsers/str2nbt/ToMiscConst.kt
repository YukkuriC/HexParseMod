package io.yukkuric.hexparse.parsers.str2nbt

import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.IotaType
import io.yukkuric.hexparse.parsers.IPlayerBinder
import io.yukkuric.hexparse.parsers.IotaFactory.*
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object ToMiscConst : IStr2Nbt, IPlayerBinder {
    val NULL = CompoundTag();

    val ConstMapper = mapOf(
        "self" to { IotaType.serialize(EntityIota(owner)) },
        "myself" to { IotaType.serialize(EntityIota(owner)) },
        "false" to { makeType(TYPE_BOOLEAN, ByteTag.ZERO) },
        "true" to { makeType(TYPE_BOOLEAN, ByteTag.ONE) },
        "null" to { makeType(TYPE_NULL, NULL) },
        "garbage" to { makeType(TYPE_GARBAGE, NULL) },
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

    override fun parse(node: String?): CompoundTag {
        val getter = ConstMapper[cachedKey] ?: throw RuntimeException("why?")
        return getter.invoke()
    }
}
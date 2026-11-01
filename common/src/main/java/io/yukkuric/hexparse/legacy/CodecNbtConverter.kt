package io.yukkuric.hexparse.legacy

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps

val KEY_DATA = "hexcasting:data"

fun CompoundTag.toIota(): Iota {
    val ret = IotaType.TYPED_CODEC.decode(NbtOps.INSTANCE, this)
    return ret.orThrow.first
}

fun Iota.toNbt(): CompoundTag {
    val ret = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, this)
    return ret.orThrow as CompoundTag
}
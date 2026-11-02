package io.yukkuric.hexparse.legacy

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps

const val KEY_TYPE = "type"
/** IMPORTANT NOTE: NOT ALL codecs are using this key in 1.21 */
const val KEY_DATA = "value"

fun CompoundTag.toIota(): Iota {
    val ret = IotaType.TYPED_CODEC.decode(NbtOps.INSTANCE, this)
    return ret.orThrow.first
}

fun Iota.toNbt(): CompoundTag {
    val ret = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, this)
    return ret.orThrow as CompoundTag
}
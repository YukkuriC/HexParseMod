package io.yukkuric.hexparse.parsers

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTextComponent
import at.petrak.hexcasting.api.utils.gold
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.netty.buffer.Unpooled
import io.yukkuric.hexparse.misc.HexParseTags
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterOutputStream


object FallbackBinaryParser {
    const val prefix = "nbt_"
    val REPLACERS = arrayOf("+-", "=_")

    object STR2NBT : Prefix(prefix), IPlayerBinder {
        override fun parse(node: String): CompoundTag {
            var raw = node.substring(4)
            for (p in REPLACERS) raw = raw.replace(p[1], p[0])
            var bytes = Base64.getDecoder().decode(raw)

            // decompress
            val out = ByteArrayOutputStream()
            val infl = InflaterOutputStream(out)
            infl.write(bytes)
            infl.flush()
            infl.close()
            bytes = out.toByteArray()

            val buf = FriendlyByteBuf(Unpooled.wrappedBuffer(bytes))
            val ret = buf.readNbt()!!
            IotaType.getTypeFromTag(ret)?.let {
                val holder = HexIotaTypes.REGISTRY.wrapAsHolder(it)
                if (holder.`is`(HexParseTags.NBT_PARSING_FORBIDDEN)) {
                    val errorMsg = "forbidden iota type: ${HexIotaTypes.REGISTRY.getKey(it)}"
                    if (callingPlayer?.hasPermissions(2) == true) {
                        callingPlayer?.sendSystemMessage(errorMsg.asTextComponent.gold)
                    } else throw RuntimeException(errorMsg)
                }
            }
            return ret
        }

        private var callingPlayer: ServerPlayer? = null
        override fun BindPlayer(p: ServerPlayer?) {
            callingPlayer = p
        }
    }

    object NBT2STR : INbt2Str {
        override fun match(node: CompoundTag) = true

        override fun parse(node: CompoundTag): String {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeNbt(node)
            var bytes = ByteArray(buf.readableBytes())
            buf.readBytes(bytes)

            // compress
            val out = ByteArrayOutputStream()
            val defl = DeflaterOutputStream(out)
            defl.write(bytes)
            defl.flush()
            defl.close()
            bytes = out.toByteArray()

            var raw = Base64.getEncoder().encodeToString(bytes)
            for (p in REPLACERS) raw = raw.replace(p[0], p[1])
            return prefix + raw
        }
    }
}
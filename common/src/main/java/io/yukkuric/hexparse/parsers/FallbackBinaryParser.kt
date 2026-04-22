package io.yukkuric.hexparse.parsers

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTextComponent
import at.petrak.hexcasting.api.utils.gold
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import io.netty.buffer.Unpooled
import io.yukkuric.hexparse.misc.HexParseTags
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterOutputStream


object FallbackBinaryParser {
    const val prefix = "nbt_"
    val REPLACERS = arrayOf("+-", "=_")

    object STR2NBT : Prefix(prefix), IPlayerBinder {
        override fun parse(node: String): Iota {
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

            val buf = RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(bytes), callingPlayer.registryAccess())
            try {
                val ret = IotaType.TYPED_STREAM_CODEC.decode(buf)
                ret.type.let {
                    val holder = HexIotaTypes.REGISTRY.wrapAsHolder(it)
                    if (holder.`is`(HexParseTags.NBT_PARSING_FORBIDDEN)) {
                        val errorMsg = "forbidden iota type: ${HexIotaTypes.REGISTRY.getKey(it)}"
                        if (callingPlayer?.hasPermissions(2) == true) {
                            callingPlayer?.sendSystemMessage(errorMsg.asTextComponent.gold)
                        } else throw RuntimeException(errorMsg)
                    }
                }
                buf.release()
                return ret
            } catch (e: Throwable) {
                buf.release()
                throw e
            }
        }

        private lateinit var callingPlayer: ServerPlayer
        override fun BindPlayer(p: ServerPlayer) {
            callingPlayer = p
        }
    }

    object NBT2STR : INbt2Str<Iota>, IPlayerBinder {
        override fun match(node: Iota) = true
        override fun getType() = Iota::class.java

        override fun parse(node: Iota): String {
            val buf = RegistryFriendlyByteBuf(Unpooled.buffer(), regAccess)
            var bytes = with(buf) {
                try {
                    IotaType.TYPED_STREAM_CODEC.encode(this, node)
                    resetReaderIndex()
                    val ret = ByteArray(buf.readableBytes())
                    readBytes(ret)
                    release()
                    return@with ret
                } catch (e: Throwable) {
                    release()
                    throw e
                }
            }

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

        private lateinit var regAccess: RegistryAccess
        override fun BindPlayer(p: ServerPlayer) {
            regAccess = p.registryAccess()
        }
    }
}
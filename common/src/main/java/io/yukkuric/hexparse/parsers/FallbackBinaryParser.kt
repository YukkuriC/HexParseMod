package io.yukkuric.hexparse.parsers

import io.netty.buffer.Unpooled
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import java.util.*

object FallbackBinaryParser {
    const val prefix = "nbt_"
    val REPLACERS = arrayOf("+-", "=_")

    object STR2NBT : Prefix(prefix) {
        override fun parse(node: String): CompoundTag {
            var raw = node.substring(4)
            for (p in REPLACERS) raw = raw.replace(p[1], p[0])
            val bytes: ByteArray = Base64.getDecoder().decode(raw)
            val buf = FriendlyByteBuf(Unpooled.wrappedBuffer(bytes))
            return buf.readNbt()!!
        }
    }

    object NBT2STR : INbt2Str {
        override fun match(node: CompoundTag) = true

        override fun parse(node: CompoundTag): String {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeNbt(node)
            val bytes = ByteArray(buf.readableBytes())
            buf.readBytes(bytes)
            var raw = Base64.getEncoder().encodeToString(bytes)
            for (p in REPLACERS) raw = raw.replace(p[0], p[1])
            return prefix + raw
        }
    }
}
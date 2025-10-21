package io.yukkuric.hexparse.parsers

import io.netty.buffer.Unpooled
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterOutputStream


object FallbackBinaryParser {
    const val prefix = "nbt_"
    val REPLACERS = arrayOf("+-", "=_")

    object STR2NBT : Prefix(prefix) {
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
            return buf.readNbt()!!
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
package io.yukkuric.hexparse.parsers.str2nbt.unsafe.hexal;

import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import net.minecraft.nbt.CompoundTag;
import ram.talia.hexal.api.casting.iota.MoteIota;
import ram.talia.hexal.api.mediafieditems.MediafiedItemManager;

import java.util.UUID;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.toNbt;

public class ToMote {
    public static BaseConstParser INSTANCE = new BaseConstParser.Prefix("mote_") {
        @Override
        public CompoundTag parse(String node) {
            var raw = node.split("_");
            var uuid = UUID.fromString(raw[1]);
            var idx = Integer.valueOf(raw[2]);
            var mote = new MoteIota(new MediafiedItemManager.Index(uuid, idx));
            return toNbt(mote);
        }
    };
}

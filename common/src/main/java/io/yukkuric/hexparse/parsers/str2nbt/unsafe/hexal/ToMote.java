package io.yukkuric.hexparse.parsers.str2nbt.unsafe.hexal;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import net.minecraft.nbt.CompoundTag;
import ram.talia.hexal.api.mediafieditems.MediafiedItemManager;
import ram.talia.hexal.api.spell.iota.MoteIota;

import java.util.UUID;

public class ToMote {
    public static BaseConstParser INSTANCE = new BaseConstParser.Prefix("mote_") {
        @Override
        public CompoundTag parse(String node) {
            var raw = node.split("_");
            var uuid = UUID.fromString(raw[1]);
            var idx = Integer.valueOf(raw[2]);
            var mote = new MoteIota(new MediafiedItemManager.Index(uuid, idx));
            return HexIotaTypes.serialize(mote);
        }
    };
}

package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import net.minecraft.nbt.CompoundTag;

public class GarbageParser implements INbt2Str<GarbageIota> {
    @Override
    public String parse(GarbageIota iota) {
        return "garbage";
    }
    @Override
    public Class<GarbageIota> getType() {
        return GarbageIota.class;
    }
}
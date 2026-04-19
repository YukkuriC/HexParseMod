package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.NullIota;

public class NullParser implements INbt2Str<NullIota> {
    @Override
    public String parse(NullIota iota) {
        return "null";
    }
    @Override
    public Class<NullIota> getType() {
        return NullIota.class;
    }
}
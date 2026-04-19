package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.BooleanIota;

public class BoolParser implements INbt2Str<BooleanIota> {
    @Override
    public String parse(BooleanIota node) {
        return node.getBool() ? "true" : "false";
    }
    @Override
    public Class<BooleanIota> getType() {
        return BooleanIota.class;
    }
}

package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;

public class NumParser implements INbt2Str<DoubleIota> {
    @Override
    public String parse(DoubleIota iota) {
        return displayMinimal(iota.getDouble());
    }
    @Override
    public Class<DoubleIota> getType() {
        return DoubleIota.class;
    }
}

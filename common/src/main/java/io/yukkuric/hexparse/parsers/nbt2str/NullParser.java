package io.yukkuric.hexparse.parsers.nbt2str;

import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

public class NullParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_NULL);
    }

    @Override
    public String parse(CompoundTag node) {
        return "null";
    }
}
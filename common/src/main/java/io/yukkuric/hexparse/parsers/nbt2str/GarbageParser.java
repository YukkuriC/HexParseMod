package io.yukkuric.hexparse.parsers.nbt2str;

import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

public class GarbageParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_GARBAGE);
    }

    @Override
    public String parse(CompoundTag node) {
        return "garbage";
    }
}
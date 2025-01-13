package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

public class BoolParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_BOOLEAN);
    }

    @Override
    public String parse(CompoundTag node) {
        return node.getByte(HexIotaTypes.KEY_DATA) != 0 ? "true" : "false";
    }
}

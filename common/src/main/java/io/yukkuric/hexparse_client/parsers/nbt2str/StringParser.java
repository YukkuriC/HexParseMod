package io.yukkuric.hexparse_client.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.nbt.CompoundTag;

public class StringParser implements INbt2Str {
    String type, prefix;

    StringParser(String type, String prefix) {
        this.type = type;
        this.prefix = prefix;
    }

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, type);
    }

    @Override
    public String parse(CompoundTag node) {
        return this.prefix + node.getString(HexIotaTypes.KEY_DATA);
    }
}

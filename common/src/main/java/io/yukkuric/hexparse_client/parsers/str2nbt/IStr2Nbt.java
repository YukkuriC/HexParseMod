package io.yukkuric.hexparse_client.parsers.str2nbt;

import net.minecraft.nbt.CompoundTag;

public interface IStr2Nbt {
    boolean match(String node);

    CompoundTag parse(String node);

    default boolean ignored() {
        return false;
    }
}

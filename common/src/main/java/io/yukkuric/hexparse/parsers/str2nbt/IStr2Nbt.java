package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.config.HexParseConfig;
import net.minecraft.nbt.CompoundTag;

public interface IStr2Nbt {
    boolean match(String node);

    CompoundTag parse(String node);

    default boolean ignored() {
        return false;
    }

    default int getCost() {
        return HexParseConfig.parserBaseCost();
    }
}

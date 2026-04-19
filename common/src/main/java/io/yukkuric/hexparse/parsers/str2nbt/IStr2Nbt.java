package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.casting.iota.Iota;
import io.yukkuric.hexparse.config.HexParseConfig;

public interface IStr2Nbt {
    boolean match(String node);

    Iota parse(String node);

    default boolean ignored() {
        return false;
    }

    default int getCost() {
        return HexParseConfig.parserBaseCost();
    }
}

package io.yukkuric.hexparse.parsers.meta;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

public interface IMetaCollector<T extends Iota> {
    default String getNamespace(T node) {
        var typeId = HexIotaTypes.REGISTRY.getResourceKey(node.getType()).orElse(null);
        if (typeId == null) return null;
        var rl = typeId.location();
        return rl.getNamespace();
    }
}

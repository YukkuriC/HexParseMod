package io.yukkuric.hexparse.parsers.meta;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.CommentIotaType;

import java.util.Objects;

public interface IMetaCollector<T extends Iota> {
    default String getNamespace(T node) {
        var typeId = HexIotaTypes.REGISTRY.getResourceKey(node.getType()).orElse(null);
        if (typeId == null || Objects.equals(typeId.toString(), CommentIotaType.TYPE_ID)) return null;
        var rl = typeId.location();
        return rl.getNamespace();
    }
}

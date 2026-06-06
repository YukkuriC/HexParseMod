package io.yukkuric.hexparse.parsers.meta;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface IMetaCollector {
    default String getNamespace(CompoundTag node) {
        var typeId = node.getString(HexIotaTypes.KEY_TYPE);
        var rl = ResourceLocation.tryParse(typeId);
        if (rl == null) return null;
        return rl.getNamespace();
    }
}

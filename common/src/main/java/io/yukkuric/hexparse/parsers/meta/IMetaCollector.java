package io.yukkuric.hexparse.parsers.meta;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public interface IMetaCollector {
    default String getNamespace(CompoundTag node) {
        var typeId = node.getString(HexIotaTypes.KEY_TYPE);
        if (Objects.equals(typeId, CommentIotaType.TYPE_ID)) return null;
        var rl = ResourceLocation.tryParse(typeId);
        if (rl == null) return null;
        return rl.getNamespace();
    }
}

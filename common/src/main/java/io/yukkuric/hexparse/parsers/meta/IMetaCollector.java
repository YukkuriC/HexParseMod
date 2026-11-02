package io.yukkuric.hexparse.parsers.meta;

import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.KEY_TYPE;

public interface IMetaCollector {
    default String getNamespace(CompoundTag node) {
        var typeId = node.getString(KEY_TYPE);
        if (Objects.equals(typeId, CommentIotaType.TYPE_ID)) return null;
        var rl = ResourceLocation.tryParse(typeId);
        if (rl == null) return null;
        return rl.getNamespace();
    }
}

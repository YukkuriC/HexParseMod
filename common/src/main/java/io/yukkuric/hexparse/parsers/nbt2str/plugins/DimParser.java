package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.KEY_DATA;

public class DimParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, PluginIotaFactory.TYPE_DIM);
    }

    @Override
    public String parse(CompoundTag node) {
        node = node.getCompound(KEY_DATA);
        var dim = node.getString("dim_key");
        return "dim_%s".formatted(StringProcessors.OMIT_MC.apply(dim));
    }
}

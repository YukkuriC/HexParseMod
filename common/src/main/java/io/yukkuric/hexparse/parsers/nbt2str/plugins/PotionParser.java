package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.KEY_DATA;

public class PotionParser implements INbt2Str {

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, PluginIotaFactory.TYPE_POTION);
    }

    @Override
    public String parse(CompoundTag node) {
        node = node.getCompound(KEY_DATA);
        var dim = node.getString("potion_key");
        return "potion_%s".formatted(StringProcessors.OMIT_MC.apply(dim));
    }
}

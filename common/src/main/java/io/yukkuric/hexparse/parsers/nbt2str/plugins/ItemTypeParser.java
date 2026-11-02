package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.PluginIotaFactory;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import io.yukkuric.yclib.YCLib;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.KEY_DATA;

public class ItemTypeParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, PluginIotaFactory.TYPE_ITEM_TYPE);
    }

    @Override
    public String parse(CompoundTag node) {
        var inner = node.get(KEY_DATA);
        if (!(inner instanceof CompoundTag tag))
            throw new RuntimeException(YCLib.doTranslate("hexparse.msg.error.invalid_iota", node));
        var typeSub = "item";
        var key = tag.getString(typeSub);
        if (key.isEmpty()) {
            typeSub = "block";
            key = tag.getString(typeSub);
        }
        if (key.isEmpty()) key = "air";
        return "type/%s_%s".formatted(typeSub, StringProcessors.OMIT_MC.apply(key));
    }
}

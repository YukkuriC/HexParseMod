package io.yukkuric.hexparse.parsers.nbt2str;

import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.KEY_DATA;

public class NumParser implements INbt2Str {
    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_DOUBLE);
    }

    @Override
    public String parse(CompoundTag node) {
        return displayMinimal(node.getDouble(KEY_DATA));
    }
}

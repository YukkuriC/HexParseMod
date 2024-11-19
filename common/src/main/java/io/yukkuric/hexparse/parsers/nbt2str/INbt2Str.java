package io.yukkuric.hexparse.parsers.nbt2str;

import net.minecraft.nbt.CompoundTag;

public interface INbt2Str {
    boolean match(CompoundTag node);

    String parse(CompoundTag node);
}

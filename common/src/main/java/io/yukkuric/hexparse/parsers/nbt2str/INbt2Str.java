package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.nbt.CompoundTag;

public interface INbt2Str {
    boolean match(CompoundTag node);

    String parse(CompoundTag node);

    // helpers

    default boolean isType(CompoundTag node, String type) {
        return node.getString(HexIotaTypes.KEY_TYPE).equals(type);
    }

    default String displayMinimal(Double raw) {
        return INbt2Str.displayMinimalStatic(raw);
    }
    static String displayMinimalStatic(Double raw) {
        String mid = "%.4f".formatted(raw);
        var ptr = mid.length();
        while (ptr > 0) {
            var c = mid.charAt(ptr - 1);
            if (c == '0') ptr--;
            else {
                if (c == '.') ptr--;
                break;
            }
        }
        return mid.substring(0, ptr);
    }

    // recursive parser support
    default CompoundTag collectInnerData() {
        return null;
    }
}

package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.interfaces.IConfigNumReceiver;
import io.yukkuric.hexparse.parsers.meta.IMetaCollector;
import io.yukkuric.hexparse.parsers.meta.MetaHolder;
import net.minecraft.nbt.CompoundTag;

public interface INbt2Str extends IConfigNumReceiver, IMetaCollector {
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

    // meta collector
    default String parseAndCollect(CompoundTag node) {
        var res = parse(node);
        if (MetaHolder.enabled(MetaHolder.ADDONS))
            MetaHolder.addNamespace(getNamespace(node));
        return res;
    }
}

package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.casting.iota.Iota;
import io.yukkuric.hexparse.parsers.interfaces.IConfigNumReceiver;

public interface INbt2Str<T extends Iota> extends IConfigNumReceiver {
    Class<T> getType();

    default boolean match(Iota node){
        return getType().isInstance(node);
    }

    String parse(T node);

    // helpers

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
}

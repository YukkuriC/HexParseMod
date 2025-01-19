package io.yukkuric.hexparse.macro;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class MacroClient {
    final Map<String, String> macros = new HashMap<>();

    public static CompoundTag serialize() {
        var pack = new CompoundTag();
        for (var pair : macros.entrySet()) {
            pack.putString(pair.getKey(), pair.getValue());
        }
        return pack;
    }
}

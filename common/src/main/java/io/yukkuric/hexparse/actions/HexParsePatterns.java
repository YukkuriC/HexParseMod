package io.yukkuric.hexparse.actions;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import io.yukkuric.hexparse.HexParse;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HexParsePatterns {
    static Map<ResourceLocation, Pair<HexPattern, Action>> CACHED = new HashMap<>();

    public static final Pair<HexPattern, Action>
//            CODE2FOCUS = wrap("code2focus", HexPattern.fromAngles("aqqqqqeawqwqwqwqwqwweeeeed", HexDir.EAST), ActionCode2Focus.INSTANCE),
            FOCUS2CODE = wrap("focus2code", HexPattern.fromAngles("aqqqqqwwewewewewewdqeeeeed", HexDir.EAST), ActionFocus2Code.INSTANCE);

    public static void registerActions() {
        try {
            for (var pair : CACHED.entrySet()) {
                var entry = pair.getValue();
                PatternRegistry.mapPattern(entry.key(), pair.getKey(), entry.value());
            }
        } catch (PatternRegistry.RegisterPatternException e) {
            throw new RuntimeException(e);
        }
    }

    static Pair<HexPattern, Action> wrap(String name, HexPattern pattern, Action action) {
        var key = new ResourceLocation(HexParse.MOD_ID, name);
        var val = Pair.of(pattern, action);
        CACHED.put(key, val);
        return val;
    }
}

package io.yukkuric.hexparse.actions;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import io.yukkuric.hexparse.HexParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class HexParsePatterns {
    static Map<ResourceLocation, ActionRegistryEntry> CACHED = new HashMap<>();

    public static final ActionRegistryEntry
            CODE2FOCUS = wrap("code2focus", HexPattern.fromAngles("aqqqqqeawqwqwqwqwqwweeeeed", HexDir.EAST), ActionCode2Focus.INSTANCE),
            FOCUS2CODE = wrap("focus2code", HexPattern.fromAngles("aqqqqqwwewewewewewdqeeeeed", HexDir.EAST), ActionFocus2Code.INSTANCE);
    public static final ActionRegistryEntry REMOVE_COMMENTS = wrap("remove_comments", HexPattern.fromAngles("dadadedadadwqaeaqeww", HexDir.NORTH_EAST), ActionRemoveComments.INSTANCE);
    public static final ActionRegistryEntry LEARN_GREAT_PATTERNS = wrap("learn_patterns", HexPattern.fromAngles("aqqqqqeawqwqwqwqwqwwqqeqqeqqeqqeqqeqqdqeeeeed", HexDir.EAST), ActionLearnGreatPatterns.INSTANCE);
    public static final ActionRegistryEntry COMPILE = wrap("compile", HexPattern.fromAngles("aqqqqqeawqwqwqwqwqwdeweweqeweweqewewe", HexDir.EAST), ActionCompile.INSTANCE);


    public static void registerActions() {
        var reg = HexActions.REGISTRY;
        for (var pair : CACHED.entrySet()) {
            Registry.register(reg, pair.getKey(), pair.getValue());
        }
    }

    static ActionRegistryEntry wrap(String name, HexPattern pattern, Action action) {
        var key = new ResourceLocation(HexParse.MOD_ID, name);
        var val = new ActionRegistryEntry(pattern, action);
        CACHED.put(key, val);
        return val;
    }
}

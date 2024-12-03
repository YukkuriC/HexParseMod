package io.yukkuric.hexparse_client.hooks;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse_client.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PatternMapper {
    public static final Map<String, CompoundTag> mapPatternMeta = new HashMap<>();
    public static final Map<String, CompoundTag> mapPattern = new HashMap<>();

    static {
        mapPatternMeta.put("escape", IotaFactory.makePattern("qqqaw", HexDir.WEST));
        mapPatternMeta.put("del", IotaFactory.makePattern("eeedw", HexDir.EAST));
        mapPatternMeta.put("pop", IotaFactory.makePattern("a", HexDir.SOUTH_WEST)); // mask_v
        mapPatternMeta.put("(", IotaFactory.makePattern("qqq", HexDir.WEST));
        mapPatternMeta.put(")", IotaFactory.makePattern("eee", HexDir.EAST));
        mapPatternMeta.put("\\", mapPatternMeta.get("escape"));
    }

    static void _setMap(Map<String, CompoundTag> map, ResourceLocation id, String seq, HexDir dir) {
        String idLong = id.toString(), idShort = id.getPath();
        var pattern = IotaFactory.makePattern(seq, dir);
        map.put(idLong, pattern);
        map.put(idShort, pattern);
    }

    static boolean inited = false;

    public static void init() {
        if (inited) return;
        inited = true;

        // clear mapper first ...?
        mapPattern.clear();

        var registry = IXplatAbstractions.INSTANCE.getActionRegistry();

        for (var entry : registry.entrySet()) {
            var key = entry.getKey();
            var pattern = entry.getValue().prototype();
            _setMap(mapPattern, key.location(), pattern.anglesSignature(), pattern.getStartDir());
        }
    }
}

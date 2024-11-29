package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.server.ScrungledPatternsSave;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class PatternMapper {
    public static final Map<String, CompoundTag> mapPatternMeta = new HashMap<>();
    public static final Map<String, CompoundTag> mapPattern = new HashMap<>();
    public static final Map<String, CompoundTag> mapPatternWorld = new HashMap<>();

    static {
        mapPatternMeta.put("escape", IotaFactory.makePattern("qqqaw", HexDir.WEST));
        mapPatternMeta.put("del", IotaFactory.makePattern("eeedw", HexDir.EAST));
        mapPatternMeta.put("pop", IotaFactory.makePattern("a", HexDir.SOUTH_WEST)); // mask_v
        mapPatternMeta.put("(", IotaFactory.makePattern("qqq", HexDir.WEST));
        mapPatternMeta.put(")", IotaFactory.makePattern("eee", HexDir.EAST));
        mapPatternMeta.put("\\", mapPattern.get("escape"));
    }

    static void _setMap(Map<String, CompoundTag> map, ResourceLocation id, String seq, HexDir dir) {
        String idLong = id.toString(), idShort = id.getPath();
        var pattern = IotaFactory.makePattern(seq, dir);
        map.put(idLong, pattern);
        map.put(idShort, pattern);
    }

    public static void init(ServerLevel level) {
        // clear mapper first ...?
        mapPattern.clear();
        mapPatternWorld.clear();

        var registry = IXplatAbstractions.INSTANCE.getActionRegistry();
        var perWorldPatterns = ScrungledPatternsSave.open(level);

        for (var entry : registry.entrySet()) {
            var key = entry.getKey();
            if (HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) {
                var perWorldEntry = perWorldPatterns.lookupReverse(key);
                if (perWorldEntry == null) continue;
                _setMap(mapPatternWorld, key.location(), perWorldEntry.getFirst(), perWorldEntry.getSecond().canonicalStartDir());
            } else {
                var pattern = entry.getValue().prototype();
                _setMap(mapPattern, key.location(), pattern.anglesSignature(), pattern.getStartDir());
            }
        }
    }
}

package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexDir;
import io.yukkuric.hexparse.misc.IotaFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class PatternMapper {
    static final ConcurrentMap<String, Object> staticMapper;
    static final Method m_opId, m_preferredStart;

    public static final Map<String, CompoundTag> mapPattern = new HashMap<>();
    public static final Map<String, CompoundTag> mapPatternWorld = new HashMap<>();

    static {
        try {
            var fStaticMapper = PatternRegistry.class.getDeclaredField("regularPatternLookup");
            fStaticMapper.setAccessible(true);
            staticMapper = (ConcurrentMap<String, Object>) fStaticMapper.get(null);
            var clsRegularEntry = Class.forName("at.petrak.hexcasting.api.PatternRegistry$RegularEntry"); //RegularEntry(HexDir preferredStart, ResourceLocation opId)
            m_opId = clsRegularEntry.getMethod("opId");
            m_opId.setAccessible(true);
            m_preferredStart = clsRegularEntry.getMethod("preferredStart");
            m_preferredStart.setAccessible(true);

            // init special patterns
            mapPattern.put("escape", IotaFactory.makePattern("qqqaw", HexDir.WEST));
            mapPattern.put("pop", IotaFactory.makePattern("a", HexDir.SOUTH_WEST)); // mask_v
            mapPattern.put("(", IotaFactory.makePattern("qqq", HexDir.WEST));
            mapPattern.put(")", IotaFactory.makePattern("eee", HexDir.EAST));
            mapPattern.put("\\", mapPattern.get("escape"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void _setMap(Map<String, CompoundTag> map, ResourceLocation id, String seq, HexDir dir) {
        String idLong = id.toString(), idShort = id.getPath();
        var pattern = IotaFactory.makePattern(seq, dir);
        map.put(idLong, pattern);
        map.put(idShort, pattern);
    }

    public static void init(CommandSourceStack source) {
        var level = source.getLevel();

        // 0. reload great patterns
        level.getServer().overworld().getDataStorage().set("hex.per-world-patterns", PatternRegistry.Save.create(level.getSeed()));

        try {
            // 1. map normal
            for (var pair : staticMapper.entrySet()) {
                var seq = pair.getKey();
                var entry = pair.getValue();
                var id = (ResourceLocation) m_opId.invoke(entry);
                var dir = (HexDir) m_preferredStart.invoke(entry);
                _setMap(mapPattern, id, seq, dir);
            }

            // 2. per-world
            for (var pair : PatternRegistry.getPerWorldPatterns(level).entrySet()) {
                var seq = pair.getKey();
                var entry = pair.getValue();
                var id = entry.getFirst();
                var dir = entry.getSecond();
                _setMap(mapPatternWorld, id, seq, dir);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

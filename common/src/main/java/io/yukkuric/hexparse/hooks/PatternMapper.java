package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.HexAPI;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.*;

public class PatternMapper {
    static final ConcurrentMap<String, Object> staticMapper;
    public static final ConcurrentMap<ResourceLocation, Object> greatMapper;
    static final Method m_opId, m_preferredStart;

    public static final Map<String, CompoundTag> mapPattern = new HashMap<>();
    public static final Map<String, CompoundTag> mapPatternWorld = new HashMap<>();

    static {
        try {
            var fStaticMapper = PatternRegistry.class.getDeclaredField("regularPatternLookup");
            fStaticMapper.setAccessible(true);
            staticMapper = (ConcurrentMap<String, Object>) fStaticMapper.get(null);
            fStaticMapper = PatternRegistry.class.getDeclaredField("perWorldPatternLookup");
            fStaticMapper.setAccessible(true);
            greatMapper = (ConcurrentMap<ResourceLocation, Object>) fStaticMapper.get(null);
            var clsRegularEntry = Class.forName("at.petrak.hexcasting.api.PatternRegistry$RegularEntry"); //RegularEntry(HexDir preferredStart, ResourceLocation opId)
            m_opId = clsRegularEntry.getMethod("opId");
            m_opId.setAccessible(true);
            m_preferredStart = clsRegularEntry.getMethod("preferredStart");
            m_preferredStart.setAccessible(true);

            // init special patterns
            mapPattern.put("\\", IotaFactory.makePattern("qqqaw", HexDir.WEST));
            mapPattern.put("(", IotaFactory.makePattern("qqq", HexDir.WEST));
            mapPattern.put(")", IotaFactory.makePattern("eee", HexDir.EAST));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void _setMap(Map<String, CompoundTag> map, ResourceLocation id, String seq, HexDir dir) {
        String idLong = id.toString(), idShort = id.getPath();
        var pattern = IotaFactory.makePattern(seq, dir);
        map.put(idLong, pattern);
        if (ShortNameTracker.recordNewShortName(id)) {
            map.put(idShort, pattern);
        }
    }

    public static void init(ServerLevel level) {
        ShortNameTracker.clear();
        
        // 0. reload great patterns
        level.getServer().overworld().getDataStorage().set(PatternRegistry.TAG_SAVED_DATA, PatternRegistry.Save.create(level.getSeed()));

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

    public static void initLocal() {
        try {
            // 1. map normal
            for (var pair : staticMapper.entrySet()) {
                var seq = pair.getKey();
                var entry = pair.getValue();
                var id = (ResourceLocation) m_opId.invoke(entry);
                var dir = (HexDir) m_preferredStart.invoke(entry);
                _setMap(mapPattern, id, seq, dir);
            }

            // 2. per-world fake
            var fooSeq = CommentIotaType.COMMENT_PATTERN.anglesSignature();
            for (var id : greatMapper.keySet()) {
                _setMap(mapPatternWorld, id, fooSeq, HexDir.EAST);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ShortNameTracker {
        public static final Map<String, CompoundTag>[] modifyTargets = new Map[]{mapPattern, mapPatternWorld};
        public static final Map<String, Set<ResourceLocation>> allPointed = new HashMap<>();
        public static final Map<String, ResourceLocation> mapActiveShortName = new HashMap<>();
        public static final Set<String> shortNameWithConflicts = new HashSet<>();

        // ==================== inner handles ====================
        /**
         * @return if this should be added as short name
         */
        static boolean recordNewShortName(ResourceLocation id) {
            var shortName = id.getPath();

            // add to all map
            var locList = allPointed.computeIfAbsent(shortName, (k) -> new HashSet<>());
            locList.add(id);
            if (locList.size() > 1) shortNameWithConflicts.add(shortName);

            // base mod with highest priority
            var idExist = mapActiveShortName.get(shortName);
            var imBoss = id.getNamespace().equals(HexAPI.MOD_ID);
            if (idExist == null || imBoss) {
                mapActiveShortName.put(shortName, id);
                return true;
            }
            return false;
        }
        static void clear() {
            allPointed.clear();
            mapActiveShortName.clear();
            shortNameWithConflicts.clear();
        }

        // ==================== APIs ====================
        public static String getActiveLongName(String shortName) {
            var id = mapActiveShortName.get(shortName);
            return id == null ? shortName : id.toString();
        }
        public static void redirectShortName(String shortName, ResourceLocation newId) {
            // check exist
            var validIdSet = allPointed.get(shortName);
            if (validIdSet == null || !shortNameWithConflicts.contains(shortName))
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.cmd.conflict.error", shortName, HexParse.doTranslate("hexparse.cmd.conflict.error.name")));
            else if (!validIdSet.contains(newId))
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.cmd.conflict.error", shortName, HexParse.doTranslate("hexparse.cmd.conflict.error.id", newId)));

            // edit short name from all maps
            var longName = newId.toString();
            var found = false;
            for (var map : modifyTargets) {
                map.remove(shortName);
                if (found) continue;
                var tryLongEntry = map.get(longName);
                if (tryLongEntry != null) {
                    found = true;
                    map.put(shortName, tryLongEntry);
                }
            }
            if (!found)
                throw new IllegalArgumentException(HexParse.doTranslate("hexparse.cmd.conflict.error", shortName, "excuse me WTF?"));

            // set active target
            mapActiveShortName.put(shortName, newId);
        }
    }
}

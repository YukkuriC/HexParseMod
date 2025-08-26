package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.server.ScrungledPatternsSave;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class PatternMapper {
    public static final Map<String, CompoundTag> mapPatternMeta = new HashMap<>();
    public static final Map<String, CompoundTag> mapPattern = new HashMap<>();
    public static final Map<String, CompoundTag> mapPatternWorld = new HashMap<>();

    static {
        mapPatternMeta.put("\\", IotaFactory.makePattern("qqqaw", HexDir.WEST));
        mapPatternMeta.put("del", IotaFactory.makePattern("eeedw", HexDir.EAST));
        mapPatternMeta.put("(", IotaFactory.makePattern("qqq", HexDir.WEST));
        mapPatternMeta.put(")", IotaFactory.makePattern("eee", HexDir.EAST));
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
        // clear mapper first ...?
        mapPattern.clear();
        mapPatternWorld.clear();
        ShortNameTracker.clear();

        var registry = IXplatAbstractions.INSTANCE.getActionRegistry();

        // auto recalc great patterns
        var overworld = level.getServer().overworld();
        var ds = overworld.getDataStorage();
        var perWorldPatterns = ScrungledPatternsSave.createFromScratch(level.getSeed());
        ds.set(ScrungledPatternsSave.TAG_SAVED_DATA, perWorldPatterns);

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

    public static void initLocal() {
        var registry = IXplatAbstractions.INSTANCE.getActionRegistry();
        for (var entry : registry.entrySet()) {
            var key = entry.getKey();
            var mapper = HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN) ? mapPatternWorld : mapPattern;
            _setMap(mapper, key.location(), "", HexDir.EAST);
        }
    }

    public static class ShortNameTracker {
        static final Map<String, CompoundTag>[] modifyTargets = new Map[]{mapPattern, mapPatternWorld};
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
        }
    }
}

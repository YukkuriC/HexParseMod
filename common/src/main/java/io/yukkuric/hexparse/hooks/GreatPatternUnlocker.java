package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse.config.HexParseConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class GreatPatternUnlocker extends SavedData {
    static final String KEY_UNLOCK_SET = "unlocks";
    static final String SAVENAME = "hexparse.great.pattern.unlocks";
    public static GreatPatternUnlocker DENY_ALL = new GreatPatternUnlocker();
    public static GreatPatternUnlocker ALLOW_ALL = new GreatPatternUnlocker() {
        @Override
        public boolean isUnlocked(String key) {
            return true;
        }
    };

    Set<String> _unlocked;

    public GreatPatternUnlocker() {
        _unlocked = new HashSet<>();
    }

    public GreatPatternUnlocker(CompoundTag save) {
        try {
            var recorder = save.getCompound(KEY_UNLOCK_SET);
            _unlocked = new HashSet<>(recorder.getAllKeys());
        } catch (Exception e) {
            _unlocked = new HashSet<>();
        }
    }

    @Override
    public CompoundTag save(CompoundTag save) {
        var recorder = new CompoundTag();
        for (var key : _unlocked) recorder.putBoolean(key, true);
        save.put(KEY_UNLOCK_SET, recorder);
        return save;
    }

    public boolean isUnlocked(String key) {
        key = PatternMapper.ShortNameTracker.getActiveLongName(key);
        return _unlocked.contains(key);
    }

    public boolean unlock(String key) {
        key = PatternMapper.ShortNameTracker.getActiveLongName(key);
        var res = _unlocked.add(key);
        if (res) setDirty();
        return res;
    }

    public boolean lock(String key) {
        key = PatternMapper.ShortNameTracker.getActiveLongName(key);
        var res = _unlocked.remove(key);
        if (res) setDirty();
        return res;
    }

    public int unlockAll() {
        int res = 0;
        for (var pair : PatternMapper.greatMapper.entrySet()) {
            var id = pair.getKey();
            if (unlock(id.toString())) res++;
        }
        if (res > 0) setDirty();
        return res;
    }

    public int clear() {
        if (_unlocked.isEmpty()) return 0;
        setDirty();
        var res = _unlocked.size();
        _unlocked.clear();
        return res;
    }

    public static GreatPatternUnlocker get(ServerLevel level) {
        var cfg = HexParseConfig.canParseGreatPatterns();
        if (cfg == HexParseConfig.ParseGreatPatternMode.DISABLED) return DENY_ALL;
        else if (cfg == HexParseConfig.ParseGreatPatternMode.ALL) return ALLOW_ALL;
        level = level.getServer().overworld();
        var ds = level.getDataStorage();
        return ds.computeIfAbsent(GreatPatternUnlocker::new, GreatPatternUnlocker::new, SAVENAME);
    }
}

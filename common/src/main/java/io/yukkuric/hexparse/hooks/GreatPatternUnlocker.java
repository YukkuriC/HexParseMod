package io.yukkuric.hexparse.hooks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashSet;
import java.util.Set;

public class GreatPatternUnlocker extends SavedData {
    static final String KEY_UNLOCK_SET = "unlocks";
    static final String SAVENAME = "hexparse.great.pattern.unlocks";
    public static GreatPatternUnlocker DENY_ALL = new GreatPatternUnlocker(null);

    Set<String> _unlocked;
    DimensionDataStorage _storage;

    public GreatPatternUnlocker(DimensionDataStorage ds) {
        _unlocked = new HashSet<>();
        _storage = ds;
    }

    public GreatPatternUnlocker(DimensionDataStorage ds, CompoundTag save) {
        _storage = ds;
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
        key = PatternMapper.mapShort2Long.getOrDefault(key, key);
        return _unlocked.contains(key);
    }

    public boolean unlock(String key) {
        doUpdate();
        key = PatternMapper.mapShort2Long.getOrDefault(key, key);
        return _unlocked.add(key);
    }

    public int unlockAll() {
        int res = 0;
        for (var pair : PatternMapper.greatMapper.entrySet()) {
            var id = pair.getKey();
            if (unlock(id.toString())) res++;
        }
        doUpdate();
        return res;
    }

    public boolean clear() {
        if (_unlocked.isEmpty()) return false;
        doUpdate();
        _unlocked.clear();
        return true;
    }

    void doUpdate() {
        setDirty();
        _storage.set(SAVENAME, this);
    }

    public static GreatPatternUnlocker get(ServerLevel level) {
        level = level.getServer().overworld();
        var ds = level.getDataStorage();
        return ds.computeIfAbsent((data) -> new GreatPatternUnlocker(ds, data), () -> new GreatPatternUnlocker(ds), SAVENAME);
    }
}

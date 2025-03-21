package io.yukkuric.hexparse.hooks;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class GreatPatternUnlocker extends SavedData {
    static final String KEY_UNLOCK_SET = "unlocks";
    static final String SAVENAME = "hexparse.great.pattern.unlocks";
    public static GreatPatternUnlocker DENY_ALL = new GreatPatternUnlocker(null);

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
        key = PatternMapper.mapShort2Long.getOrDefault(key, key);
        return _unlocked.contains(key);
    }

    public boolean unlock(String key) {
        key = PatternMapper.mapShort2Long.getOrDefault(key, key);
        var res = _unlocked.add(key);
        if (res) setDirty();
        return res;
    }

    public boolean lock(String key) {
        key = PatternMapper.mapShort2Long.getOrDefault(key, key);
        var res = _unlocked.remove(key);
        if (res) setDirty();
        return res;
    }

    public int unlockAll() {
        int res = 0;
        var registry = IXplatAbstractions.INSTANCE.getActionRegistry();
        for (var entry : registry.entrySet()) {
            var key = entry.getKey();
            if (HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) {
                if (unlock(key.location().toString())) res++;
            }
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
        level = level.getServer().overworld();
        var ds = level.getDataStorage();
        return ds.computeIfAbsent(GreatPatternUnlocker::new, GreatPatternUnlocker::new, SAVENAME);
    }
}

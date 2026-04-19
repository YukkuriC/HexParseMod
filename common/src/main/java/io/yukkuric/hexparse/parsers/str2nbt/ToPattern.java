package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.casting.iota.Iota;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ToPattern implements IStr2Nbt {
    final Map<String, Iota> target;

    ToPattern(Map<String, Iota> target) {
        this.target = target;
    }

    @Override
    public boolean match(String node) {
        return this.target.containsKey(node);
    }

    @Override
    public Iota parse(String node) {
        return this.target.get(node);
    }

    public static ToPattern NORMAL = new ToPattern(PatternMapper.mapPattern);
    public static ToPattern META = new ToPattern(PatternMapper.mapPatternMeta);
    public static ToGreatPattern GREAT = new ToGreatPattern(PatternMapper.mapPatternWorld);

    public static class ToGreatPattern extends ToPattern implements IPlayerBinder {
        GreatPatternUnlocker checker;

        ToGreatPattern(Map<String, Iota> target) {
            super(target);
        }

        @Override
        public void BindPlayer(@NotNull ServerPlayer p) {
            var level = p.serverLevel();
            checker = GreatPatternUnlocker.get(level);
        }

        @Override
        public Iota parse(String node) {
            if (checker == null || checker.isUnlocked(node)) return super.parse(node);
            return IotaFactory.makeUnknownGreatPattern(node);
        }
    }
}

package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.config.HexParseConfig;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class ToPattern implements IStr2Nbt {
    final Map<String, CompoundTag> target;

    ToPattern(Map<String, CompoundTag> target) {
        this.target = target;
    }

    @Override
    public boolean match(String node) {
        return this.target.containsKey(node);
    }

    @Override
    public CompoundTag parse(String node) {
        return this.target.get(node);
    }

    public static ToPattern NORMAL = new ToPattern(PatternMapper.mapPattern);
    public static ToGreatPattern GREAT = new ToGreatPattern(PatternMapper.mapPatternWorld);

    public static class ToGreatPattern extends ToPattern implements IPlayerBinder {
        GreatPatternUnlocker checker;

        ToGreatPattern(Map<String, CompoundTag> target) {
            super(target);
        }

        @Override
        public void BindPlayer(ServerPlayer p) {
            if (HexParseConfig.canParseGreatPatterns() != HexParseConfig.ParseGreatPatternMode.BY_SCROLL) {
                checker = null;
                return;
            }
            var level = p.getLevel();
            checker = GreatPatternUnlocker.get(level);
        }

        @Override
        public boolean match(String node) {
            if (HexParseConfig.canParseGreatPatterns() == HexParseConfig.ParseGreatPatternMode.DISABLED) return false;
            return super.match(node);
        }

        @Override
        public CompoundTag parse(String node) {
            if (checker == null || checker.isUnlocked(node)) return super.parse(node);
            return IotaFactory.makeComment(String.format("<%s?>", node));
        }
    }

    ;
}

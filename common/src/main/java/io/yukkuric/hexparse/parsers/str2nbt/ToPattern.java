package io.yukkuric.hexparse.parsers.str2nbt;

import at.petrak.hexcasting.api.casting.iota.Iota;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

import static io.yukkuric.hexparse.legacy.CodecNbtConverterKt.toNbt;

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
    public CompoundTag parse(String node) {
        return toNbt(this.target.get(node));
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
        public void BindPlayer(ServerPlayer p) {
            var level = p.serverLevel();
            checker = GreatPatternUnlocker.get(level);
        }

        @Override
        public CompoundTag parse(String node) {
            if (checker == null || checker.isUnlocked(node)) return super.parse(node);
            return IotaFactory.makeUnknownGreatPattern(node);
        }
    }
}

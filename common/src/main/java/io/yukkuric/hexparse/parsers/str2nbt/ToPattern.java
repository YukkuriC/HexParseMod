package io.yukkuric.hexparse.parsers.str2nbt;

import io.yukkuric.hexparse.config.HexParseConfig;
import io.yukkuric.hexparse.hooks.PatternMapper;
import net.minecraft.nbt.CompoundTag;

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
    public static ToPattern META = new ToPattern(PatternMapper.mapPatternMeta);
    public static ToPattern GREAT = new ToPattern(PatternMapper.mapPatternWorld) {
        @Override
        public boolean match(String node) {
            if (HexParseConfig.canParseGreatPatterns()== HexParseConfig.ParseGreatPatternMode.DISABLED) return false;
            return super.match(node);
        }
    };
}

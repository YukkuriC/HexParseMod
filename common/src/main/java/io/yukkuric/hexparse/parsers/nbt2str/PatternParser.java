package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidPattern;
import at.petrak.hexcasting.common.casting.operators.stack.OpMask;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.misc.IotaFactory;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PatternParser implements INbt2Str, IPlayerBinder {
    static Map<String, String> SPECIAL_PATTERNS = new HashMap<>() {
        {
            put("qqq", "(");
            put("eee", ")");
            put("qqqaw", "\\");
        }
    };

    @Override
    public boolean match(CompoundTag node) {
        return node.getString(HexIotaTypes.KEY_TYPE).equals(IotaFactory.TYPE_PATTERN);
    }

    @Override
    public String parse(CompoundTag node) {
        var pattern = HexPattern.fromNBT(node.getCompound(HexIotaTypes.KEY_DATA));
        var angleSigs = pattern.anglesSignature();
        if (SPECIAL_PATTERNS.containsKey(angleSigs)) return SPECIAL_PATTERNS.get(angleSigs);
        try {
            var matcher = PatternRegistry.matchPatternAndID(pattern, level);
            var action = matcher.getFirst();
            var opId = matcher.getSecond();
            var opIdStr = opId.toString();
            if (opIdStr.equals("hexcasting:mask")) {
                var maskBuilder = new StringBuilder("mask_");
                for (var m : ((OpMask) action).getMask()) {
                    maskBuilder.append(m ? '-' : 'v');
                }
                return maskBuilder.toString();
            } else if (opIdStr.equals("hexcasting:number")) {
                var constInner = ((ConstMediaAction) action).execute(null, null);
                return String.format("num_%f", ((DoubleIota) constInner.get(0)).getDouble());
            } else if (!PatternMapper.mapPattern.containsKey(opIdStr) && !PatternMapper.mapPatternWorld.containsKey(opIdStr)) {
                throw new MishapInvalidPattern();
            }

            if (opId.getNamespace().equals(HexAPI.MOD_ID)) return opId.getPath();
            return opIdStr;
        } catch (MishapInvalidPattern e) {
        }

        return '_' + angleSigs;
    }

    ServerLevel level;

    @Override
    public void BindPlayer(Player p) {
        this.level = (ServerLevel) p.level;
    }
}

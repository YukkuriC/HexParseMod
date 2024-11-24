package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidPattern;
import at.petrak.hexcasting.common.casting.operators.stack.OpMask;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.IotaFactory;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternParser implements INbt2Str, IPlayerBinder {
    static Map<String, String> SPECIAL_PATTERNS = new HashMap<>() {
        {
            put("qqq", "(");
            put("eee", ")");
            put("qqqaw", "\\");
        }
    };
    static List<Iota> FOO_LIST = List.of();

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_PATTERN);
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
                var constInner = ((ConstMediaAction) action).execute(FOO_LIST, new CastingContext(player, InteractionHand.MAIN_HAND, CastingContext.CastSource.STAFF));
                return "num_" + displayMinimal(((DoubleIota) constInner.get(0)).getDouble());
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
    ServerPlayer player;

    @Override
    public void BindPlayer(ServerPlayer p) {
        this.player = p;
        this.level = (ServerLevel) p.level;
    }
}

package io.yukkuric.hexparse.parsers.nbt2str;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.casting.actions.math.SpecialHandlerNumberLiteral;
import at.petrak.hexcasting.common.casting.actions.stack.SpecialHandlerMask;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.IotaFactory;
import io.yukkuric.hexparse.parsers.interfaces.ConfigNums;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PatternParser implements INbt2Str, IPlayerBinder {
    static Map<String, String> SPECIAL_PATTERNS = new HashMap<>() {
        {
            put("qqq", "(");
            put("eee", ")");
            put("qqqaw", "\\");
        }
    };
    static Map<Class<?>, BiFunction<?, CompoundTag, String>> SPECIAL_HANDLER_MAP = new HashMap<>();

    public static <T extends SpecialHandler> void AddSpecialHandlerBackParser(Class<T> cls, BiFunction<T, CompoundTag, String> func) {
        SPECIAL_HANDLER_MAP.put(cls, func);
    }

    static {
        AddSpecialHandlerBackParser(SpecialHandlerMask.class, (hm, node) -> {
            var maskBuilder = new StringBuilder("mask_");
            for (var m : hm.getMask()) {
                maskBuilder.append(m ? '-' : 'v');
            }
            return maskBuilder.toString();
        });
        AddSpecialHandlerBackParser(SpecialHandlerNumberLiteral.class, (hn, node) -> {
            return "num_" + INbt2Str.displayMinimalStatic(hn.getX());
        });
    }

    private int configState = 0;
    @Override
    public void receiveConfigNum(int num) {
        configState = num;
    }

    @Override
    public boolean match(CompoundTag node) {
        return isType(node, IotaFactory.TYPE_PATTERN);
    }

    @Override
    public String parse(CompoundTag node) {
        var pattern = HexPattern.fromNBT(node.getCompound(HexIotaTypes.KEY_DATA));
        var angleSigs = pattern.anglesSignature();

        // early escape by config
        if (hasConfigNum(configState, ConfigNums.FORCE_SIGNATURES)) return '_' + angleSigs;

        if (SPECIAL_PATTERNS.containsKey(angleSigs)) return SPECIAL_PATTERNS.get(angleSigs);

        // do match
        var matcher = PatternRegistryManifest.matchPattern(pattern, new StaffCastEnv(player, InteractionHand.MAIN_HAND), false);
        ResourceLocation opId = null;
        if (matcher instanceof PatternShapeMatch.Normal nm) {
            opId = nm.key.location();
        } else if (matcher instanceof PatternShapeMatch.PerWorld pm) {
            opId = pm.key.location();
        } else if (matcher instanceof PatternShapeMatch.Special sm) {
            var handler = sm.handler;
            var func = (BiFunction<SpecialHandler, CompoundTag, String>) SPECIAL_HANDLER_MAP.get(handler.getClass());
            if (func != null) return func.apply(handler, node);
        }

        // by key
        if (opId != null) {
            if (opId.getNamespace().equals(HexAPI.MOD_ID)) return opId.getPath();
            return opId.toString();
        }

        // by angle sig
        return '_' + angleSigs;
    }

    ServerLevel level;
    ServerPlayer player;

    @Override
    public void BindPlayer(ServerPlayer p) {
        this.player = p;
        this.level = p.serverLevel();
    }
}

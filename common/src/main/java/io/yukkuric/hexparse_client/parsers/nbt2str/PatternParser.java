package io.yukkuric.hexparse_client.parsers.nbt2str;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.casting.actions.math.SpecialHandlerNumberLiteral;
import at.petrak.hexcasting.common.casting.actions.stack.SpecialHandlerMask;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.yukkuric.hexparse_client.parsers.IotaFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternParser implements INbt2Str {
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

        // do match
        var matcher = PatternRegistryManifest.matchPattern(pattern, null, false);
        ResourceLocation opId = null;
        if (matcher instanceof PatternShapeMatch.Normal nm) {
            opId = nm.key.location();
        } else if (matcher instanceof PatternShapeMatch.PerWorld pm) {
            opId = pm.key.location();
        } else if (matcher instanceof PatternShapeMatch.Special sm) {
            var handler = sm.handler;
            if (handler instanceof SpecialHandlerMask hm) { // mask
                var maskBuilder = new StringBuilder("mask_");
                for (var m : hm.getMask()) {
                    maskBuilder.append(m ? '-' : 'v');
                }
                return maskBuilder.toString();
            } else if (handler instanceof SpecialHandlerNumberLiteral hn) { // num
                return "num_" + displayMinimal(hn.getX());
            }
        }

        // by key
        if (opId != null) {
            if (opId.getNamespace().equals(HexAPI.MOD_ID)) return opId.getPath();
            return opId.toString();
        }

        // by angle sig
        return '_' + angleSigs;
    }
}

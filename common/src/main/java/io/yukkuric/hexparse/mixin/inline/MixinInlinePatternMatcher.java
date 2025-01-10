package io.yukkuric.hexparse.mixin.inline;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.interop.inline.HexPatternMatcher;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.yukkuric.hexparse.parsers.IotaFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// temp fix until dev has alternative way
@Mixin(HexPatternMatcher.class)
public class MixinInlinePatternMatcher {
    @WrapOperation(method = "getMatchAndGroup", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/math/HexPattern;fromAngles(Ljava/lang/String;Lat/petrak/hexcasting/api/casting/math/HexDir;)Lat/petrak/hexcasting/api/casting/math/HexPattern;", remap = false))
    HexPattern skipChecks(String signature, HexDir startDir, Operation<HexPattern> original) {
        return HexPattern.fromNBT(IotaFactory.makePattern(signature, startDir).getCompound(HexIotaTypes.KEY_DATA));
    }
}

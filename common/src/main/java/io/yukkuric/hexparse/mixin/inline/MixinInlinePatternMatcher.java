package io.yukkuric.hexparse.mixin.inline;

import at.petrak.hexcasting.interop.inline.HexPatternMatcher;
import org.spongepowered.asm.mixin.Mixin;

// temp fix until dev has alternative way
@Mixin(HexPatternMatcher.class)
public class MixinInlinePatternMatcher {
    /*@WrapOperation(method = "getMatchAndGroup", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/math/HexPattern;fromAngles(Ljava/lang/String;Lat/petrak/hexcasting/api/casting/math/HexDir;)Lat/petrak/hexcasting/api/casting/math/HexPattern;", remap = false), require = 0)
    HexPattern skipChecks(String signature, HexDir startDir, Operation<HexPattern> original) {
        return HexPattern.fromNBT(IotaFactory.makePattern(signature, startDir).getCompound(HexIotaTypes.KEY_DATA));
    }*/
}

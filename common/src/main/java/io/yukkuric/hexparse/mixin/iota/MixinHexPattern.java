package io.yukkuric.hexparse.mixin.iota;

import at.petrak.hexcasting.api.casting.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(HexPattern.Companion.class)
public class MixinHexPattern {
    @Inject(method = "fromAngles", at = @At(value = "HEAD"), remap = false, require = 0, cancellable = true)
    void dontYouDareDoThis(String signature, HexDir startDir, CallbackInfoReturnable<HexPattern> cir) {
        var angles = new ArrayList<HexAngle>();
        for (int i = 0, l = signature.length(); i < l; i++) {
            HexAngle tmp;
            var c = signature.charAt(i);
            switch (c) {
                case 'a' -> tmp = HexAngle.LEFT_BACK;
                case 'd' -> tmp = HexAngle.RIGHT_BACK;
                case 'e' -> tmp = HexAngle.RIGHT;
                case 'q' -> tmp = HexAngle.LEFT;
                case 's' -> tmp = HexAngle.BACK;
                case 'w' -> tmp = HexAngle.FORWARD;
                default -> throw new IllegalArgumentException("Cannot match " + c + " at idx " + i + " to a direction");
            }
            angles.add(tmp);
        }
        cir.setReturnValue(new HexPattern(startDir, angles));
    }
}

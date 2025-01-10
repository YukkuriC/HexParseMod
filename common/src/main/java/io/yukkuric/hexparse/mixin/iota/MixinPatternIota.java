package io.yukkuric.hexparse.mixin.iota;

import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import io.yukkuric.hexparse.mixin_interface.NestedCounter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PatternIota.class)
public class MixinPatternIota {
    private static final List<ChatFormatting> COLORS = List.of(
            ChatFormatting.AQUA,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.GREEN,
            ChatFormatting.YELLOW,
            ChatFormatting.BLUE
    );

    @Inject(method = "display", at = @At("RETURN"))
    private static void hookParens(HexPattern pat, CallbackInfoReturnable<Component> cir) {
        var angles = pat.getAngles();
        if (angles.size() != 3) return;
        boolean isLeft = true, isRight = true;
        for (var dir : angles) {
            isLeft &= dir == HexAngle.LEFT;
            isRight &= dir == HexAngle.RIGHT;
        }
        if (!isLeft && !isRight) return;
        if (isLeft) NestedCounter.EnterParen();
        var cnt = NestedCounter.GetParensCount();
        if (cnt < 0) return;
        var comp = (MutableComponent) cir.getReturnValue();
        comp.withStyle(COLORS.get(cnt % COLORS.size()));
        if (isRight) NestedCounter.LeaveParen();
    }
}

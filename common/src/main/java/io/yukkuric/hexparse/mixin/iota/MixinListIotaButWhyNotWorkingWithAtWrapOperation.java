package io.yukkuric.hexparse.mixin.iota;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "at.petrak.hexcasting.api.casting.iota.ListIota$1")
public class MixinListIotaButWhyNotWorkingWithAtWrapOperation {

    private static final List<ChatFormatting> COLORS = List.of(
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.GOLD,
            ChatFormatting.DARK_RED,
            ChatFormatting.BLUE,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.YELLOW,
            ChatFormatting.AQUA,
            ChatFormatting.GREEN
    );
    private static int parenCounter = 0;

    @Inject(method = "display", at = @At("HEAD"))
    void coloredParen_Pre(Tag tag, CallbackInfoReturnable<Component> cir) {
        parenCounter++;
    }

    @Inject(method = "display", at = @At("RETURN"))
    void coloredParen_Post(Tag tag, CallbackInfoReturnable<Component> cir) {
        var res = (MutableComponent) cir.getReturnValue();
        res.withStyle(COLORS.get((parenCounter - 1) % COLORS.size()));
        parenCounter = Math.max(0, parenCounter - 1);
    }
}

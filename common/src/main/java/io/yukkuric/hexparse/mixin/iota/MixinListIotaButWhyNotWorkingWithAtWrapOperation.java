package io.yukkuric.hexparse.mixin.iota;

import io.yukkuric.hexparse.mixin_interface.NestedCounter;
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
            ChatFormatting.DARK_BLUE,
            ChatFormatting.DARK_RED,
            ChatFormatting.DARK_GREEN,
            ChatFormatting.DARK_AQUA
    );

    @Inject(method = "display", at = @At("HEAD"))
    void coloredParen_Pre(Tag tag, CallbackInfoReturnable<Component> cir) {
        NestedCounter.EnterNested();
    }

    @Inject(method = "display", at = @At("RETURN"))
    void coloredParen_Post(Tag tag, CallbackInfoReturnable<Component> cir) {
        var res = (MutableComponent) cir.getReturnValue();
        var cnt = NestedCounter.GetNestedCount();
        if (cnt < 0) return; // but why?
        res.withStyle(COLORS.get(cnt % COLORS.size()));
        NestedCounter.LeaveNested();
    }
}

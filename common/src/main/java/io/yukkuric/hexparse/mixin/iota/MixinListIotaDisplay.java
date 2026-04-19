package io.yukkuric.hexparse.mixin.iota;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.yukkuric.hexparse.hooks.CommentIota;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ListIota.class)
public class MixinListIotaDisplay {
    @Shadow(remap = false)
    private SpellList list;
    @WrapOperation(method = "display", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;append(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    public MutableComponent cancelCommentCommas(MutableComponent instance, String body, Operation<MutableComponent> original, @Local(ordinal = 0) int index) {
        if (!body.startsWith(",") || index >= list.size() - 1)
            return original.call(instance, body);
        if (list.getAt(index) instanceof CommentIota || list.getAt(index + 1) instanceof CommentIota)
            return instance;
        return original.call(instance, body);
    }
}

package io.yukkuric.hexparse.mixin.iota;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.yukkuric.hexparse.hooks.CommentIotaType;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "at.petrak.hexcasting.api.spell.iota.ListIota$1")
public class MixinListIotaDisplay {
    @WrapOperation(method = "display", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;append(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
    public MutableComponent cancelCommentCommas(MutableComponent instance, String body, Operation<MutableComponent> original,
                                                Tag tag, @Local(ordinal = 0) int index) {
        if (!body.startsWith(",") || !(tag instanceof ListTag list) || index >= list.size() - 1)
            return original.call(instance, body);
        if (HexIotaTypes.getTypeFromTag((CompoundTag) list.get(index)) == CommentIotaType.INSTANCE ||
                HexIotaTypes.getTypeFromTag((CompoundTag) list.get(index + 1)) == CommentIotaType.INSTANCE)
            return instance;
        return original.call(instance, body);
    }
}

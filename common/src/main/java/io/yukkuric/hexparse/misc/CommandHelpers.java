package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.common.items.ItemFocus;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CommandHelpers {
    public static ItemStack getFocusItem(CommandContext<CommandSourceStack> ctx) {
        var entity = ctx.getSource().getEntity();
        if (entity == null || !(entity instanceof LivingEntity le)) return null;
        var checkHand = le.getMainHandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        checkHand = le.getOffhandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        return null;
    }

    public static void injectItem(ItemStack target, CompoundTag nbt, String rename) {
        if (target != null) {
            var tag = target.getOrCreateTag();
            tag.put("data", nbt);
            if (rename != null) {
                rename = rename.replace("\\", "\\\\").replace("\"", "\\\"");
                var inner = new CompoundTag();
                inner.putString("Name", String.format("{\"text\":\"%s\"}", rename));
                tag.put("display", inner);
            }
        }
    }
}

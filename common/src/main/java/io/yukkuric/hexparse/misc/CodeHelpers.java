package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.common.items.ItemFocus;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface CodeHelpers {
    static ItemStack getFocusItem(Player player) {
        if (player == null) return null;
        var checkHand = player.getMainHandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        checkHand = player.getOffhandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        return null;
    }

    static void doParse(Player player, String code, String rename) {
        var target = getFocusItem(player);
        if (target == null) return;
        var nbt = ParserMain.ParseCode(code, player);
        var tag = target.getOrCreateTag();
        tag.put("data", nbt);
        if (rename != null) target.setHoverName(Component.literal(rename));
    }

    static String readHand(Player player) {
        var target = getFocusItem(player);
        if (target == null) return null;
        var iotaRoot = ((ItemFocus) (target.getItem())).readIotaTag(target);
        if (iotaRoot == null) return null;
        return ParserMain.ParseIotaNbt(iotaRoot, player);
    }
}

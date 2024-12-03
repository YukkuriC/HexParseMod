package io.yukkuric.hexparse_client.misc;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.common.msgs.MsgNewSpellPatternC2S;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import io.yukkuric.hexparse_client.hooks.PatternMapper;
import io.yukkuric.hexparse_client.parsers.ParserMain;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface CodeHelpers {
    static ItemStack getFocusItem(LocalPlayer player) {
        if (player == null) return null;
        var checkHand = player.getMainHandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        checkHand = player.getOffhandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        return null;
    }

    static void doParse(LocalPlayer player, String code) {
        PatternMapper.init();
        var patterns = ParserMain.ParseCode(code, player);
        for (var p : patterns) {
            var fakeResolved = List.of(new ResolvedPattern(p, HexCoord.getOrigin(), ResolvedPatternType.UNDONE));
            IClientXplatAbstractions.INSTANCE.sendPacketToServer(new MsgNewSpellPatternC2S(InteractionHand.MAIN_HAND, p, fakeResolved));
        }
    }

    static String readHand(LocalPlayer player) {
        var target = getFocusItem(player);
        if (target == null) return null;
        var iotaRoot = ((ItemFocus) (target.getItem())).readIotaTag(target);
        if (iotaRoot == null) return null;
        PatternMapper.init();
        return ParserMain.ParseIotaNbt(iotaRoot, player);
    }
}

package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.common.items.storage.ItemFocus;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.WeakReference;

public interface CodeHelpers {
    static ItemStack getFocusItem(ServerPlayer player) {
        if (player == null) return null;
        var checkHand = player.getMainHandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        checkHand = player.getOffhandItem();
        if (checkHand.getItem() instanceof ItemFocus) return checkHand;
        return null;
    }

    static void doParse(ServerPlayer player, String code, String rename) {
        var target = getFocusItem(player);
        if (target == null) return;
        autoRefresh(player.getServer());
        var nbt = ParserMain.ParseCode(code, player);
        var tag = target.getOrCreateTag();
        tag.put("data", nbt);
        if (rename != null) target.setHoverName(Component.literal(rename));
    }

    static String readHand(ServerPlayer player) {
        var target = getFocusItem(player);
        if (target == null) return null;
        var iotaRoot = ((ItemFocus) (target.getItem())).readIotaTag(target);
        if (iotaRoot == null) return null;
        autoRefresh(player.getServer());
        return ParserMain.ParseIotaNbt(iotaRoot, player);
    }

    WeakReference<MinecraftServer> refreshedWorld = new WeakReference<>(null);

    static void autoRefresh(MinecraftServer server) {
        if (server != refreshedWorld.get()) {
            var level = server.overworld();
            HexParse.LOGGER.info("auto refresh for server: %s, level: %s".formatted(server.name(), level));
            PatternMapper.init(level);
            refreshedWorld.refersTo(server);
        }
    }
}

package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.common.items.ItemFocus;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.WeakReference;
import java.util.List;

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

    static void doParse(ServerPlayer player, List<String> code, String rename) {
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
    WeakReference<Boolean> refreshedLocal = new WeakReference<>(false);

    static void autoRefresh(MinecraftServer server) {
        if (server != refreshedWorld.get()) {
            var level = server.overworld();
            HexParse.LOGGER.info("auto refresh for server: %s, level: %s".formatted(server.name(), level));
            PatternMapper.init(level);
            refreshedLocal.refersTo(true);
            refreshedWorld.refersTo(server);
        }
    }

    static void autoRefreshLocal() {
        if (refreshedLocal.get()) return;
        PatternMapper.initLocal();
        refreshedLocal.refersTo(true);
    }

    static void displayCode(ServerPlayer player, String code) {
        if (player == null || code == null) return;
        var display = Component.literal("Result: ").withStyle(ChatFormatting.GREEN).append(Component.literal(code).withStyle(ChatFormatting.WHITE));
        player.sendSystemMessage(wrapClickCopy(display, code));
    }

    static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.literal("CLICK TO COPY")))
        );
    }
}

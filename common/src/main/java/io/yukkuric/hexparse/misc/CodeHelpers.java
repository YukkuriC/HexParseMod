package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.advancements.HexAdvancementTriggers;
import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import at.petrak.hexcasting.api.misc.HexDamageSources;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.mod.HexStatistics;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public interface CodeHelpers {
    static void doExtractMedia(ServerPlayer caster, int amount) {
        var harness = IXplatAbstractions.INSTANCE.getHarness(caster, InteractionHand.MAIN_HAND);
        // picked from CastingHarness.withdrawMedia
        // https://github.com/FallingColors/HexMod/blob/1.19/Common/src/main/java/at/petrak/hexcasting/api/spell/casting/CastingHarness.kt#L548-L575
        {
            var costLeft = amount;
            var mediaSources = DiscoveryHandlers.collectMediaHolders(harness);
            mediaSources.sort(Collections.reverseOrder(MediaHelper::compareMediaItem));
            for (var source : mediaSources) {
                costLeft -= MediaHelper.extractMedia(source, costLeft, true, false);
                if (costLeft <= 0) break;
            }
            if (costLeft > 0) {
                // Cast from HP!
                var mediaToHealth = HexConfig.common().mediaToHealthRate();
                var healthToRemove = Math.max(costLeft / mediaToHealth, 0.5);
                var mediaAbleToCastFromHP = caster.getHealth() * mediaToHealth;

                Mishap.Companion.trulyHurt(caster, HexDamageSources.OVERCAST, (float) healthToRemove);
                var actuallyTaken = (int) Math.ceil(mediaAbleToCastFromHP - (caster.getHealth() * mediaToHealth));

                HexAdvancementTriggers.OVERCAST_TRIGGER.trigger(caster, actuallyTaken);
                caster.awardStat(HexStatistics.MEDIA_OVERCAST, amount - costLeft);
            }
        }
    }

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
        var display = Component.translatable("hexparse.cmd.read.display", Component.literal(code).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(wrapClickCopy(display, code));
    }

    static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.translatable("chat.copy.click")))
        );
    }
}

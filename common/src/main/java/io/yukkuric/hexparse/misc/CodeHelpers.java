package io.yukkuric.hexparse.misc;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import io.yukkuric.hexparse.hooks.PatternMapper;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.List;

import static io.yukkuric.hexparse.misc.CodeHelpersKt.getItemIO;

public class CodeHelpers {
    public static void doParse(ServerPlayer player, String code, String rename) {
        var target = getItemIO(player, true);
        if (target == null) return;
        var nbt = ParserMain.ParseCode(code, player);
        target.write(nbt);
        if (rename != null) target.rename(rename);
    }

    public static void doParse(ServerPlayer player, List<String> code, String rename) {
        var target = getItemIO(player, true);
        if (target == null) return;
        var nbt = ParserMain.ParseCode(code, player);
        target.write(nbt);
        if (rename != null) target.rename(rename);
    }

    public static String readHand(ServerPlayer player) {
        return readHand(player, 0, StringProcessors.READ_DEFAULT);
    }
    public static String readHand(ServerPlayer player, int configNum, StringProcessors.F post) {
        var target = getItemIO(player, false);
        if (target == null) return null;
        var iotaRoot = target.read();
        if (iotaRoot == null) return null;
        autoRefresh(player.getServer());
        return ParserMain.ParseIotaNbt(iotaRoot, player, post);
    }

    static WeakReference<MinecraftServer> refreshedWorld = new WeakReference<>(null);
    static boolean refreshedLocal = false;

    public static void autoRefresh(MinecraftServer server) {
        if (!refreshedWorld.refersTo(server)) {
            var level = server.overworld();
            HexParse.LOGGER.info("auto refresh for server: {}, level: {}", server.name(), level);
            PatternMapper.init(level);
            refreshedLocal = true;
            refreshedWorld = new WeakReference<>(server);
        }
    }

    public static void autoRefreshLocal() {
        if (refreshedLocal) return;
        PatternMapper.initLocal();
        refreshedLocal = true;
    }

    public static void displayCode(ServerPlayer player, String code) {
        if (player == null || code == null) return;
        var display = Component.translatable("hexparse.cmd.read.display", Component.literal(code).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GREEN);
        player.sendSystemMessage(wrapClickCopy(display, code));
    }

    public static MutableComponent wrapClickCopy(MutableComponent component, String code) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.translatable("chat.copy.click")))
        );
    }
    public static MutableComponent wrapClickSuggest(MutableComponent component, String command) {
        return component.withStyle(
                Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.literal(command)))
        );
    }

    public static Component getPatternDisplay(ResourceLocation id, ServerLevel level) {
        var longName = id.toString();

        // check great pattern display
        if (PatternMapper.mapPatternWorld.containsKey(longName)) {
            if (!GreatPatternUnlocker.get(level).isUnlocked(longName)) return Component.literal("???");
        }

        CompoundTag rawIota = null;
        for (var map : PatternMapper.ShortNameTracker.modifyTargets) {
            if (map.containsKey(longName)) {
                rawIota = map.get(longName);
                break;
            }
        }
        if (rawIota == null) return Component.literal("NULL");
        return IotaType.getDisplay(rawIota);
    }

    public static Component dumpError(MutableComponent raw, Throwable e) {
        // dump stack trace
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        var trace = sw.toString();

        return raw.withStyle(
                Style.EMPTY.withColor(ChatFormatting.DARK_RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, trace))
                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.deserializeFromLegacy(Component.literal(trace)))
        );
    }
}

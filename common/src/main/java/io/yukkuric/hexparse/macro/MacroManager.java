package io.yukkuric.hexparse.macro;

import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class MacroManager {
    public static final int MAX_MACRO_COUNT = 1024;
    public static final int MAX_SINGLE_MACRO_SIZE = 1024;

    static final Map<ServerPlayer, CompoundTag> playerMacros = new HashMap<>();

    public static void receivePlayerMacros(ServerPlayer player, CompoundTag pack) {
        playerMacros.put(player, pack);
    }

    static CompoundTag getPool(ServerPlayer player) {
        return playerMacros.getOrDefault(player, null);
    }

    public static boolean isMacro(String key) {
        return key.startsWith("#");
    }

    public static List<Pair<String, String>> listMacros(ServerPlayer player, boolean isMacro) {
        var pool = getPool(player);
        if (pool == null) return List.of();
        var res = new ArrayList<Pair<String, String>>();
        for (var k : pool.getAllKeys()) {
            if (isMacro(k) != isMacro) continue;
            res.add(Pair.of(k, pool.getString(k)));
        }
        return res;
    }

    public static String getMacro(ServerPlayer player, String key) {
        var pool = getPool(player);
        if (pool == null) return null;
        if (!pool.contains(key, Tag.TAG_STRING)) return null;
        return pool.getString(key);
    }

    public static boolean willThisExceedLimit(ServerPlayer player, String key) {
        var pool = getPool(player);
        if (pool == null) return false;
        if (pool.contains(key)) return false;
        return pool.size() >= MAX_MACRO_COUNT;
    }

    public static void modifyMacro(ServerPlayer player, boolean isDefine, String key, String value) {
        var pool = getPool(player);
        if (pool == null) return;
        if (isDefine) {
            pool.putString(key, value);
        } else {
            pool.remove(key);
        }
        MsgHandlers.SERVER.sendPacketToPlayer(player, new MsgUpdateClientMacro(isDefine, key, value));

        // msg
        if (isDefine)
            player.sendSystemMessage(Component.translatable("hexparse.cmd.macro.define", showGold(key), showGold(value)));
        else player.sendSystemMessage(Component.translatable("hexparse.cmd.macro.remove", showGold(key)));
    }

    // helpers
    public static MutableComponent showGold(String val) {
        return CodeHelpers.wrapClickCopy(Component.literal(val).withStyle(ChatFormatting.GOLD), val);
    }
}

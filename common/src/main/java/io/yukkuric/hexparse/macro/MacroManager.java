package io.yukkuric.hexparse.macro;

import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class MacroManager {
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

    public static void modifyMacro(ServerPlayer player, boolean isDefine, String key, String value) {
        var pool = getPool(player);
        if (pool == null) return;
        if (isDefine) {
            pool.putString(key, value);
        } else {
            pool.remove(key);
        }
        MsgHandlers.SERVER.sendPacketToPlayer(player, new MsgUpdateClientMacro(isDefine, key, value));
    }
}

package io.yukkuric.hexparse.macro;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class MacroManager {
    final Map<ServerPlayer, CompoundTag> playerMacros = new HashMap<>();

    public static void receivePlayerMacros(ServerPlayer player, CompoundTag pack) {
        // TODO
    }
}

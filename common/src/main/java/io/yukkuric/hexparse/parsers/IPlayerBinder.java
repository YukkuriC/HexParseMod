package io.yukkuric.hexparse.parsers;

import net.minecraft.server.level.ServerPlayer;

public interface IPlayerBinder {
    void BindPlayer(ServerPlayer p);
}

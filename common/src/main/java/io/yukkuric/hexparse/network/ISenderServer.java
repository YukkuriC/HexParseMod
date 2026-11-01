package io.yukkuric.hexparse.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface ISenderServer {
    void sendPacketToPlayer(ServerPlayer player, CustomPacketPayload packet);
}

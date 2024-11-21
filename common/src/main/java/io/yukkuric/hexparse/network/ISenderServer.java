package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.network.IMessage;
import net.minecraft.server.level.ServerPlayer;

public interface ISenderServer {
    void sendPacketToPlayer(ServerPlayer player, IMessage packet);
}

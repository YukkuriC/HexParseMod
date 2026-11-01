package io.yukkuric.hexparse.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ISenderClient {
    void sendPacketToServer(CustomPacketPayload packet);
}




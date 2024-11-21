package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.network.IMessage;

public interface ISenderClient {
    void sendPacketToServer(IMessage packet);
}




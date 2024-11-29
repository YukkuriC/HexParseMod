package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.msgs.IMessage;

public interface ISenderClient {
    void sendPacketToServer(IMessage packet);
}




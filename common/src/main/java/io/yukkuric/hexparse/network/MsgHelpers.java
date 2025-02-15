package io.yukkuric.hexparse.network;

import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public interface MsgHelpers {
    static String getString(FriendlyByteBuf buf) {
        var len = buf.readInt();
        if (len < 0) return null;
        return (String) buf.readCharSequence(len, StandardCharsets.UTF_8);
    }

    static void putString(FriendlyByteBuf buf, String str) {
        if (str == null) {
            buf.writeInt(-1);
            return;
        }
        int posBeforeLen = buf.writerIndex();
        buf.writeInt(114514);
        int posBeforeStr = buf.writerIndex();
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
        int posAfterStr = buf.writerIndex();
        buf.setInt(posBeforeLen, posAfterStr - posBeforeStr);
    }
}

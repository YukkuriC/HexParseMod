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
        buf.writeInt(str.length());
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }
}

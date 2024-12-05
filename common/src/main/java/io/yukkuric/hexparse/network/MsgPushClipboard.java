package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public record MsgPushClipboard(List<String> code, String rename) implements IMessage {
    public static final ResourceLocation ID = new ResourceLocation(HexParse.MOD_ID, "clipboard/push");

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeInt(code.size());
        for (var f : code)
            MsgHelpers.putString(buf, f);
        MsgHelpers.putString(buf, rename);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static MsgPushClipboard deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var code = new ArrayList<String>();
        var len = buf.readInt();
        for (int i = 0; i < len; i++) code.add(MsgHelpers.getString(buf));
        var name = MsgHelpers.getString(buf);
        return new MsgPushClipboard(code, name);
    }

    public static void handle(MsgPushClipboard self, ServerPlayer sender) {
        CodeHelpers.doParse(sender, self.code, self.rename);
    }
}

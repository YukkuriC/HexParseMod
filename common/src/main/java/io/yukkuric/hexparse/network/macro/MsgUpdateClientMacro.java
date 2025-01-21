package io.yukkuric.hexparse.network.macro;

import at.petrak.hexcasting.common.network.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.macro.MacroClient;
import io.yukkuric.hexparse.network.MsgHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record MsgUpdateClientMacro(boolean isDefine, String key, String value) implements IMessage {
    public static final ResourceLocation ID = new ResourceLocation(HexParse.MOD_ID, "macro/update");

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeBoolean(isDefine);
        MsgHelpers.putString(buf, key);
        if (isDefine) MsgHelpers.putString(buf, value);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static MsgUpdateClientMacro deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var isDefine = buf.readBoolean();
        var key = MsgHelpers.getString(buf);
        var value = isDefine ? MsgHelpers.getString(buf) : null;
        return new MsgUpdateClientMacro(isDefine, key, value);
    }

    public static void handle(MsgUpdateClientMacro self) {
        MacroClient.entryOp(self.isDefine, self.key, self.value);
    }
}

package io.yukkuric.hexparse.network.macro;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.macro.MacroClient;
import io.yukkuric.hexparse.network.MsgHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MsgUpdateClientMacro(boolean isDefine, String key, String value) implements IMessage, CustomPacketPayload {
    public static final ResourceLocation ID = HexParse.modLoc("macro/update");

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

    public static final CustomPacketPayload.Type<MsgUpdateClientMacro> TYPE = new CustomPacketPayload.Type<>(ID);
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, MsgUpdateClientMacro> STREAM_CODEC = new StreamCodec<>() {
        public MsgUpdateClientMacro decode(RegistryFriendlyByteBuf buf) {
            return deserialize(buf);
        }
        public void encode(RegistryFriendlyByteBuf buf, MsgUpdateClientMacro msg) {
            msg.serialize(buf);
        }
    };
}

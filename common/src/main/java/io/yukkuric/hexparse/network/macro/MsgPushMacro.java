package io.yukkuric.hexparse.network.macro;

import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.macro.MacroClient;
import io.yukkuric.hexparse.macro.MacroManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class MsgPushMacro implements CustomPacketPayload {
    public static final ResourceLocation ID = HexParse.modLoc("macro/push");
    final CompoundTag pack;

    MsgPushMacro(CompoundTag p) {
        pack = p;
    }

    public MsgPushMacro() {
        this(MacroClient.serialize());
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeNbt(pack);
    }

    public static MsgPushMacro deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var pack = buf.readNbt();
        return new MsgPushMacro(pack);
    }

    public static void handle(MsgPushMacro self, ServerPlayer sender) {
        MacroManager.receivePlayerMacros(sender, self.pack);
    }

    public static final CustomPacketPayload.Type<MsgPushMacro> TYPE = new CustomPacketPayload.Type<>(ID);
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, MsgPushMacro> STREAM_CODEC = new StreamCodec<>() {
        public MsgPushMacro decode(RegistryFriendlyByteBuf buf) {
            return deserialize(buf);
        }
        public void encode(RegistryFriendlyByteBuf buf, MsgPushMacro msg) {
            msg.serialize(buf);
        }
    };
}

package io.yukkuric.hexparse.network;

import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record MsgSyncDisplayMap(Map<String, String> map, Map<String, String> prefixMap) implements CustomPacketPayload {
    public static final ResourceLocation ID = HexParse.modLoc("display/sync");

    public void serialize(FriendlyByteBuf buf) {
        buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
        buf.writeMap(prefixMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
    }

    public static MsgSyncDisplayMap deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var map = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);
        var prefixMap = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);
        return new MsgSyncDisplayMap(map, prefixMap);
    }

    public static void handle(MsgSyncDisplayMap self) {
        var MC = Minecraft.getInstance();
        MC.execute(() -> {
            DotHexPatternMapper.receiveRemoteMap(self);
        });
    }

    public static final CustomPacketPayload.Type<MsgSyncDisplayMap> TYPE = new CustomPacketPayload.Type<>(ID);
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, MsgSyncDisplayMap> STREAM_CODEC = new StreamCodec<>() {
        public MsgSyncDisplayMap decode(RegistryFriendlyByteBuf buf) {
            return deserialize(buf);
        }
        public void encode(RegistryFriendlyByteBuf buf, MsgSyncDisplayMap msg) {
            msg.serialize(buf);
        }
    };
}

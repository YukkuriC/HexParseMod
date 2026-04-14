package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record MsgSyncDisplayMap(Map<String, String> map, Map<String, String> prefixMap) implements IMessage {
    public static final ResourceLocation ID = HexParse.modLoc("display/sync");

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeMap(map, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
        buf.writeMap(prefixMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
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
}

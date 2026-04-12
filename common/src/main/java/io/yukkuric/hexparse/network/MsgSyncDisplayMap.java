package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.msgs.IMessage;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record MsgSyncDisplayMap(Map<String, String> map) implements IMessage {
    public static final ResourceLocation ID = HexParse.modLoc("display/sync");
    public static final Codec<Map<String, String>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING);

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, map);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static MsgSyncDisplayMap deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var map = buf.readJsonWithCodec(CODEC);
        return new MsgSyncDisplayMap(map);
    }

    public static void handle(MsgSyncDisplayMap self) {
        var MC = Minecraft.getInstance();
        MC.execute(() -> {
            DotHexPatternMapper.receiveRemoteMap(self.map);
        });
    }
}

package io.yukkuric.hexparse.network.macro;

import at.petrak.hexcasting.common.network.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.macro.MacroClient;
import io.yukkuric.hexparse.macro.MacroManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class MsgPushMacro implements IMessage {
    public static final ResourceLocation ID = new ResourceLocation(HexParse.MOD_ID, "macro/push");
    final CompoundTag pack;

    MsgPushMacro(CompoundTag p) {
        pack = p;
    }

    public MsgPushMacro() {
        this(MacroClient.serialize());
    }

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeNbt(pack);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static MsgPushMacro deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var pack = buf.readNbt();
        return new MsgPushMacro(pack);
    }

    public static void handle(MsgPushMacro self, ServerPlayer sender) {
        MacroManager.receivePlayerMacros(sender, self.pack);
    }
}

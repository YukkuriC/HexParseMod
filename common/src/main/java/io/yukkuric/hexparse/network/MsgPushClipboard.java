package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.common.network.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.macro.MacroManager;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.parsers.ParserMain;
import miyucomics.hexcellular.StateStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public record MsgPushClipboard(List<String> code, String rename, ClipboardMsgMode mode) implements IMessage {
    public static final ResourceLocation ID = new ResourceLocation(HexParse.MOD_ID, "clipboard/push");

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeInt(code.size());
        for (var f : code)
            MsgHelpers.putString(buf, f);
        MsgHelpers.putString(buf, rename);
        buf.writeByte(mode.ordinal());
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
        ClipboardMsgMode mode;
        try {
            mode = ClipboardMsgMode.values()[buf.readByte()];
        } catch (IndexOutOfBoundsException e) {
            mode = ClipboardMsgMode.DEFAULT;
        } catch (Throwable e) {
            mode = ClipboardMsgMode.INVALID;
        }
        return new MsgPushClipboard(code, name, mode);
    }

    public static void handle(MsgPushClipboard self, ServerPlayer sender) {
        if (self.mode == ClipboardMsgMode.INVALID) return;
        else if (self.mode == ClipboardMsgMode.MACRO_DEFINE) {
            var macro = String.join(",", self.code);
            MacroManager.modifyMacro(sender, true, self.rename, macro);
        } else if (self.mode == ClipboardMsgMode.WRITE_PROPERTY) {
            var nbt = ParserMain.ParseCode(self.code, sender);
            var world = sender.getLevel();
            StateStorage.Companion.setProperty(world, self.rename, HexIotaTypes.deserialize(nbt, world));
        } else CodeHelpers.doParse(sender, self.code, self.rename);
    }
}

package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.network.IMessage;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public record MsgPullClipboard(String rename, ClipboardMsgMode mode) implements IMessage {
    public static final ResourceLocation ID = new ResourceLocation(HexParse.MOD_ID, "clipboard/pull");
    static Pattern ANGLES = Pattern.compile("(?<=\")[wedsaq]*(?=\")");
    static int MAX_LENGTH = 100 * HexIotaTypes.MAX_SERIALIZATION_TOTAL;

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeByte(mode.ordinal());
        MsgHelpers.putString(buf, rename);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static MsgPullClipboard deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        ClipboardMsgMode mode;
        try {
            mode = ClipboardMsgMode.values()[buf.readByte()];
        } catch (Throwable e) {
            mode = ClipboardMsgMode.INVALID;
        }
        var name = MsgHelpers.getString(buf);
        return new MsgPullClipboard(name, mode);
    }

    public static void handle(MsgPullClipboard self) {
        var MC = Minecraft.getInstance();
        MC.execute(() -> {
            var code = MC.keyboardHandler.getClipboard();
            if (code.isBlank()) return;
            if (self.mode == ClipboardMsgMode.ANGLES_ONLY) {
                var matched = ANGLES.matcher(code).results().map(x -> '_' + x.group());
                code = String.join(" ", matched.toList());
            }
            if (code.length() > MAX_LENGTH) {
                if (MC.player != null)
                    MC.player.sendSystemMessage(Component.translatable("hexparse.msg.error.code_too_long", code.length()));
                return;
            }
            CodeHelpers.autoRefreshLocal();
            if (self.mode != ClipboardMsgMode.INVALID)
                MsgHandlers.CLIENT.sendPacketToServer(new MsgPushClipboard(ParserMain.preMatchClipboardClient(code), self.rename, self.mode));
        });
    }
}

package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.common.msgs.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.parsers.ParserMain;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public record MsgPullClipboard(String rename, ClipboardMsgMode mode) implements IMessage {
    public static final ResourceLocation ID = HexParse.modLoc("clipboard/pull");
    static Pattern ANGLES = Pattern.compile("(?<=\")[wedsaq]*(?=\")");
    static int MAX_LENGTH = 100 * HexIotaTypes.MAX_SERIALIZATION_TOTAL;
    static int MAX_LENGTH_RAW = MAX_LENGTH * 10;

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
            if (code.length() > MAX_LENGTH_RAW) {
                sendTooLongMsg(MC.player, code.length());
                return;
            }
            if (self.mode == ClipboardMsgMode.ANGLES_ONLY) {
                var matched = ANGLES.matcher(code).results().map(x -> '_' + x.group());
                code = String.join(" ", matched.toList());
            } else if (self.mode == ClipboardMsgMode.HEXPATTERN_FORMAT) {
                code = DotHexPatternMapper.processCode(code, true);
            }
            if (code.length() > MAX_LENGTH) {
                sendTooLongMsg(MC.player, code.length());
                return;
            }
            CodeHelpers.autoRefreshLocal();
            if (self.mode != ClipboardMsgMode.INVALID)
                MsgHandlers.CLIENT.sendPacketToServer(new MsgPushClipboard(ParserMain.preMatchClipboardClient(code), self.rename, self.mode));
        });
    }

    private static void sendTooLongMsg(LocalPlayer player, int length) {
        if (player != null)
            player.sendSystemMessage(Component.translatable("hexparse.msg.error.code_too_long", length));
    }
}

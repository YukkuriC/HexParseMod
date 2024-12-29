package io.yukkuric.hexparse.network;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.misc.CodeHelpers;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public record MsgPullClipboard(String rename, boolean anglesOnly) implements IMessage {
    public static final ResourceLocation ID = new ResourceLocation(HexParse.MOD_ID, "clipboard/pull");
    static Pattern ANGLES = Pattern.compile("(?<=\")[wedsaq]*(?=\")");
    static int MAX_LENGTH = 1048576;

    @Override
    public void serialize(FriendlyByteBuf buf) {
        buf.writeBoolean(anglesOnly);
        MsgHelpers.putString(buf, rename);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static MsgPullClipboard deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var flag = buf.readBoolean();
        var name = MsgHelpers.getString(buf);
        return new MsgPullClipboard(name, flag);
    }

    public static void handle(MsgPullClipboard self) {
        var MC = Minecraft.getInstance();
        var code = MC.keyboardHandler.getClipboard();
        if (code.isBlank()) return;
        if (self.anglesOnly) {
            var matched = ANGLES.matcher(code).results().map(x -> '_' + x.group());
            code = String.join(" ", matched.toList());
        }
        if (code.length() > MAX_LENGTH) {
            if (MC.player != null)
                MC.player.sendSystemMessage(Component.translatable("hexparse.msg.error.code_too_long", code.length()));
            return;
        }
        CodeHelpers.autoRefreshLocal();
        MsgHandlers.CLIENT.sendPacketToServer(new MsgPushClipboard(ParserMain.preMatchClipboardClient(code), self.rename));
    }
}

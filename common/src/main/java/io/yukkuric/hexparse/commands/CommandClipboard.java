package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.network.MsgPullClipboard;
import io.yukkuric.hexparse.network.MsgHandlers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandClipboard {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(getBranch("clipboard", false));
        cmd.then(getBranch("clipboard_angles", true));
    }

    static ArgumentBuilder<CommandSourceStack, ?> getBranch(String name, boolean anglesOnly) {
        Command<CommandSourceStack> handler = (CommandContext<CommandSourceStack> ctx) -> {
            var player = ctx.getSource().getPlayer();
            if (player == null) return 0;
            String rename = null;
            try {
                rename = StringArgumentType.getString(ctx, "rename");
            } catch (Exception ignored) {
            }
            MsgHandlers.SERVER.sendPacketToPlayer(player, new MsgPullClipboard(rename, anglesOnly));
            return 999;
        };
        return Commands.literal(name).executes(handler)
                .then(Commands.argument("rename", StringArgumentType.string()).executes(handler));
    }
}

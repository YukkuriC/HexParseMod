package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.network.MsgHandlers;
import io.yukkuric.hexparse.network.MsgPullClipboard;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandClipboard {
    public static void init() {
        getBranch("clipboard", false);
        getBranch("clipboard_angles", true);
    }

    static void getBranch(String name, boolean anglesOnly) {
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
        registerLine(handler, 2,
                Commands.literal(name),
                Commands.argument("rename", StringArgumentType.string())
        );
    }
}

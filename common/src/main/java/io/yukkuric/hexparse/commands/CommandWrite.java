package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandWrite {
    public static void init() {
        registerLine(CommandWrite::doParse, 2,
                Commands.argument("code", StringArgumentType.string()),
                Commands.argument("rename", StringArgumentType.string())
        );
    }

    static int doParse(CommandContext<CommandSourceStack> ctx) {
        var player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        var code = StringArgumentType.getString(ctx, "code");
        String rename = null;
        try {
            rename = StringArgumentType.getString(ctx, "rename");
        } catch (Exception ignored) {
        }

        CodeHelpers.doParse(player, code, rename);
        return 114514;
    }
}

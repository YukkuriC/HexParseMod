package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandWrite {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.argument("code", StringArgumentType.string())
                        .executes(CommandWrite::doParse)
                        .then(
                                Commands.argument("rename", StringArgumentType.string())
                                        .executes(CommandWrite::doParse)
                        )
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

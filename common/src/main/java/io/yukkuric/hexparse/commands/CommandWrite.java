package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.parsers.ParserMain;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static io.yukkuric.hexparse.misc.CommandHelpers.*;

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
        var target = getFocusItem(ctx);
        if (target == null) return 0;

        var nbt = ParserMain.ParseCode(StringArgumentType.getString(ctx, "code"), ctx.getSource());
        boolean doRename = false;
        String rename = null;
        try {
            rename = StringArgumentType.getString(ctx, "rename");
        } catch (Exception ignored) {
        }

        injectItem(target, nbt, rename);
        return 114514;
    }
}

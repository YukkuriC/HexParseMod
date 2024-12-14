package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.hooks.PatternMapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandRefresh {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(Commands.literal("refreshMappings")
                .requires(s -> s.hasPermission(2))
                .executes(CommandRefresh::doRefresh));
    }

    static int doRefresh(CommandContext<CommandSourceStack> ctx) {
        PatternMapper.init(ctx.getSource().getLevel());
        return 19260817;
    }
}

package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.hooks.PatternMapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandRefresh {
    public static void init() {
        registerLine(CommandRefresh::doRefresh,
                Commands.literal("refreshMappings").requires(s -> s.hasPermission(2))
        );
    }

    static int doRefresh(CommandContext<CommandSourceStack> ctx) {
        PatternMapper.init(ctx.getSource().getLevel());
        return 19260817;
    }
}

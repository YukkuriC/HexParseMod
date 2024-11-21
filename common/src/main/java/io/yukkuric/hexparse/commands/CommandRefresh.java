package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.hooks.PatternMapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

import java.lang.ref.WeakReference;

public class CommandRefresh {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(Commands.literal("refreshMappings").executes(CommandRefresh::doRefresh));
    }

    static int doRefresh(CommandContext<CommandSourceStack> ctx) {
        PatternMapper.init(ctx.getSource());
        return 19260817;
    }

    static WeakReference<MinecraftServer> refreshedWorld = new WeakReference<>(null);

    static void autoRefresh(CommandContext<CommandSourceStack> ctx) {
        var currentServer = ctx.getSource().getServer();
        if (currentServer != refreshedWorld.get()) {
            doRefresh(ctx);
            refreshedWorld.refersTo(currentServer);
        }
    }
}

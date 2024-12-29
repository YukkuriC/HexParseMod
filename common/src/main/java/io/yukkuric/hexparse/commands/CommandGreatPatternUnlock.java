package io.yukkuric.hexparse.commands;

import at.petrak.hexcasting.common.command.PatternResLocArgument;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.config.HexParseConfig;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;

public class CommandGreatPatternUnlock {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        var sub = Commands.literal("unlock_great").requires(s -> s.hasPermission(2) && HexParseConfig.canParseGreatPatterns() == HexParseConfig.ParseGreatPatternMode.BY_SCROLL);

        // unlock
        sub.then(Commands.literal("unlockAll").executes(saveOp((save, ctx) -> {
            var res = save.unlockAll();
            return HexParse.doTranslate("hexparse.cmd.unlocker.unlock.all", res);
        })));
        sub.then(Commands.literal("lockAll").executes(saveOp((save, ctx) -> {
            var res = save.clear();
            return HexParse.doTranslate("hexparse.cmd.unlocker.lock.all", res);
        })));
        sub.then(Commands.literal("unlock").then(Commands.argument("patternKey", PatternResLocArgument.id()).executes(saveOp((save, ctx) -> {
            var key = ResourceLocationArgument.getId(ctx, "patternKey").toString();
            save.unlock(key);
            return HexParse.doTranslate("hexparse.cmd.unlocker.unlock.one", key);
        }))));
        sub.then(Commands.literal("lock").then(Commands.argument("patternKey", PatternResLocArgument.id()).executes(saveOp((save, ctx) -> {
            var key = ResourceLocationArgument.getId(ctx, "patternKey").toString();
            save.lock(key);
            return HexParse.doTranslate("hexparse.cmd.unlocker.lock.one", key);
        }))));

        cmd.then(sub);
    }

    static Command<CommandSourceStack> saveOp(BiFunction<GreatPatternUnlocker, CommandContext<CommandSourceStack>, String> callback) {
        return ctx -> {
            var save = GreatPatternUnlocker.get(ctx.getSource().getLevel());
            var msg = callback.apply(save, ctx);
            if (msg == null) return 0;
            var player = ctx.getSource().getPlayer();
            if (player != null) player.sendSystemMessage(Component.literal(msg));
            return 1;
        };
    }
}

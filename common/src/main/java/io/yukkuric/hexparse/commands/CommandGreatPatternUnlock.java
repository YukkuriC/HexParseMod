package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.config.HexParseConfig;
import io.yukkuric.hexparse.hooks.GreatPatternUnlocker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;

import java.util.function.*;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandGreatPatternUnlock {
    public static void init() {
        var sub = Commands.literal("unlock_great").requires(s -> s.hasPermission(2) && HexParseConfig.canParseGreatPatterns() == HexParseConfig.ParseGreatPatternMode.BY_SCROLL);

        registerLine(saveOp((save, ctx) -> opBatch(save::unlockAll, "hexparse.cmd.unlocker.unlock.all")),
                sub, Commands.literal("unlockAll"));
        registerLine(saveOp((save, ctx) -> opBatch(save::clear, "hexparse.cmd.unlocker.lock.all")),
                sub, Commands.literal("lockAll"));
        registerLine(saveOp((save, ctx) -> opSingle(ctx, save::unlock, "hexparse.cmd.unlocker.unlock.one")),
                sub, Commands.literal("unlock"));
        registerLine(saveOp((save, ctx) -> opSingle(ctx, save::lock, "hexparse.cmd.unlocker.lock.one")),
                sub, Commands.literal("lock"));
    }

    static String opBatch(Supplier<Integer> op, final String msg) {
        var res = op.get();
        return HexParse.doTranslate(msg, res);
    }

    static String opSingle(CommandContext<CommandSourceStack> ctx, Function<String, Boolean> op, final String msg) {
        var key = ResourceLocationArgument.getId(ctx, "patternKey").toString();
        op.apply(key);
        return HexParse.doTranslate(msg, key);
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

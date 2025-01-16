package io.yukkuric.hexparse.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse.misc.CodeHelpers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static io.yukkuric.hexparse.hooks.HexParseCommands.registerLine;

public class CommandDonate {
    public static void init() {
        registerLine(CommandDonate::doDonate,
                Commands.literal("donate"),
                Commands.argument("amount", IntegerArgumentType.integer(1))
        );
    }

    static int doDonate(CommandContext<CommandSourceStack> ctx) {
        var caster = ctx.getSource().getPlayer();
        if (caster == null) return 0;
        var amount = IntegerArgumentType.getInteger(ctx, "amount") * 10000; // as dust unit
        CodeHelpers.doExtractMedia(caster, amount);
        return 1;
    }
}

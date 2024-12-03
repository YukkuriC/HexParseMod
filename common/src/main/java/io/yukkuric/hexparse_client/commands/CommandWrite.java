package io.yukkuric.hexparse_client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.yukkuric.hexparse_client.misc.CodeHelpers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static io.yukkuric.hexparse_client.HexParse.Client;

public class CommandWrite {
    public static void init(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(
                Commands.literal("clipboard").executes(CommandWrite::doParse)
        );
    }

    static int doParse(CommandContext<CommandSourceStack> ctx) {
        var player = Client.player;
        if (player == null) return 0;

        var code = Client.keyboardHandler.getClipboard();

        CodeHelpers.doParse(player, code);
        return 114514;
    }
}

package io.yukkuric.hexparse_client.hooks;

import com.mojang.brigadier.CommandDispatcher;
import io.yukkuric.hexparse_client.commands.CommandRead;
import io.yukkuric.hexparse_client.commands.CommandWrite;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class HexParseCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var mainCmd = Commands.literal("hexParse_client");

        CommandRead.init(mainCmd);
        CommandWrite.init(mainCmd);

        dispatcher.register(mainCmd);
    }
}
